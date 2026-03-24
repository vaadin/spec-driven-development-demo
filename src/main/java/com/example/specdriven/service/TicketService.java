package com.example.specdriven.service;

import com.example.specdriven.model.*;
import com.example.specdriven.repository.CommentRepository;
import com.example.specdriven.repository.TicketRepository;
import com.example.specdriven.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public Ticket submitTicket(String title, String description, Category category, Priority priority) {
        User currentUser = getCurrentUser();
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setCategory(category);
        ticket.setPriority(priority);
        ticket.setStatus(Status.OPEN);
        ticket.setCreatedBy(currentUser);
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public List<Ticket> getMyTickets(Status statusFilter) {
        User currentUser = getCurrentUser();
        if (statusFilter != null) {
            return ticketRepository.findByCreatedByAndStatusOrderByUpdatedDateDesc(currentUser, statusFilter);
        }
        return ticketRepository.findByCreatedByOrderByUpdatedDateDesc(currentUser);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketDetail(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        // Force-load comments
        ticket.getComments().size();
        return ticket;
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByStatuses(List<Status> statuses) {
        return ticketRepository.findByStatusIn(statuses);
    }

    @Transactional
    public Ticket assignTicketToMe(Long ticketId) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setAssignedTo(currentUser);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Comment addComment(Long ticketId, String text) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() == Status.CLOSED) {
            throw new RuntimeException("Cannot add comments to closed tickets");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTicket(ticket);
        comment.setAuthor(currentUser);
        ticket.setUpdatedDate(LocalDateTime.now());
        ticketRepository.save(ticket);
        return commentRepository.save(comment);
    }

    @Transactional
    public Ticket changeStatus(Long ticketId, Status newStatus) {
        User currentUser = getCurrentUser();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        validateStatusTransition(ticket.getStatus(), newStatus);

        ticket.setStatus(newStatus);

        // Auto-assign on IN_PROGRESS if unassigned
        if (newStatus == Status.IN_PROGRESS && ticket.getAssignedTo() == null) {
            ticket.setAssignedTo(currentUser);
        }

        return ticketRepository.save(ticket);
    }

    public List<Status> getValidNextStatuses(Status currentStatus) {
        return switch (currentStatus) {
            case OPEN -> List.of(Status.IN_PROGRESS);
            case IN_PROGRESS -> List.of(Status.RESOLVED, Status.OPEN);
            case RESOLVED -> List.of(Status.CLOSED, Status.OPEN);
            case CLOSED -> List.of();
        };
    }

    private void validateStatusTransition(Status current, Status next) {
        if (!getValidNextStatuses(current).contains(next)) {
            throw new RuntimeException("Invalid status transition from " + current + " to " + next);
        }
    }

    // Dashboard metrics
    @Transactional(readOnly = true)
    public long countByStatus(Status status) {
        return ticketRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countResolvedToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return ticketRepository.countByStatusAndUpdatedDateAfter(Status.RESOLVED, startOfDay);
    }

    @Transactional(readOnly = true)
    public long countClosedToday() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return ticketRepository.countByStatusAndUpdatedDateAfter(Status.CLOSED, startOfDay);
    }

    @Transactional(readOnly = true)
    public Map<Category, Long> getOpenTicketsByCategory() {
        List<Status> nonClosed = List.of(Status.OPEN, Status.IN_PROGRESS, Status.RESOLVED);
        Map<Category, Long> result = new java.util.LinkedHashMap<>();
        for (Category category : Category.values()) {
            result.put(category, ticketRepository.countByStatusInAndCategory(nonClosed, category));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Priority, Long> getOpenTicketsByPriority() {
        List<Status> nonClosed = List.of(Status.OPEN, Status.IN_PROGRESS, Status.RESOLVED);
        Map<Priority, Long> result = new java.util.LinkedHashMap<>();
        for (Priority priority : Priority.values()) {
            result.put(priority, ticketRepository.countByStatusInAndPriority(nonClosed, priority));
        }
        return result;
    }
}
