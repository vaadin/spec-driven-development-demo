package com.example.specdriven.endpoint;

import com.example.specdriven.model.*;
import com.example.specdriven.service.TicketService;
import com.vaadin.hilla.BrowserCallable;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;
import java.util.List;

@BrowserCallable
@PermitAll
public class TicketEndpoint {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public TicketSummary submitTicket(String title, String description, Category category, Priority priority) {
        Ticket ticket = ticketService.submitTicket(title, description, category, priority);
        return toSummary(ticket);
    }

    public List<TicketSummary> getMyTickets(Status statusFilter) {
        return ticketService.getMyTickets(statusFilter).stream()
                .map(this::toSummary)
                .toList();
    }

    public TicketDetail getTicketDetail(Long ticketId) {
        Ticket ticket = ticketService.getTicketDetail(ticketId);
        return toDetail(ticket);
    }

    private TicketSummary toSummary(Ticket ticket) {
        return new TicketSummary(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedDate() != null ? ticket.getCreatedDate().format(FORMATTER) : null,
                ticket.getUpdatedDate() != null ? ticket.getUpdatedDate().format(FORMATTER) : null
        );
    }

    private TicketDetail toDetail(Ticket ticket) {
        List<CommentDto> comments = ticket.getComments().stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getText(),
                        c.getAuthor().getName(),
                        c.getCreatedDate() != null ? c.getCreatedDate().format(FORMATTER) : null
                ))
                .toList();

        return new TicketDetail(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getCreatedBy() != null ? ticket.getCreatedBy().getName() : null,
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : null,
                ticket.getCreatedDate() != null ? ticket.getCreatedDate().format(FORMATTER) : null,
                ticket.getUpdatedDate() != null ? ticket.getUpdatedDate().format(FORMATTER) : null,
                comments
        );
    }

    public record TicketSummary(Long id, String title, Category category, Priority priority, Status status,
                                String createdDate, String updatedDate) {}

    public record TicketDetail(Long id, String title, String description, Category category, Priority priority,
                               Status status, String createdByName, String assignedToName,
                               String createdDate, String updatedDate, List<CommentDto> comments) {}

    public record CommentDto(Long id, String text, String authorName, String createdDate) {}
}
