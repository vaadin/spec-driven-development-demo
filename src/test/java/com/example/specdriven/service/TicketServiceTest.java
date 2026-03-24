package com.example.specdriven.service;

import com.example.specdriven.model.*;
import com.example.specdriven.repository.CommentRepository;
import com.example.specdriven.repository.TicketRepository;
import com.example.specdriven.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private User customer;
    private User agent;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();

        customer = userRepository.findByEmail("customer@test.com").orElseThrow();
        agent = userRepository.findByEmail("agent@test.com").orElseThrow();
    }

    private void authenticateAs(String email, String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }

    private void authenticateAsCustomer() {
        authenticateAs("customer@test.com", "CUSTOMER");
    }

    private void authenticateAsAgent() {
        authenticateAs("agent@test.com", "ADMIN");
    }

    private Ticket createTicketAsCustomer() {
        authenticateAsCustomer();
        return ticketService.submitTicket("Test Ticket", "Test description",
                Category.TECHNICAL, Priority.MEDIUM);
    }

    @Nested
    class SubmitTicket {

        @Test
        void submitTicketCreatesTicketWithOpenStatus() {
            Ticket ticket = createTicketAsCustomer();

            assertNotNull(ticket.getId());
            assertEquals(Status.OPEN, ticket.getStatus());
        }

        @Test
        void submitTicketSetsCreatedByToCurrentUser() {
            Ticket ticket = createTicketAsCustomer();

            assertEquals(customer.getId(), ticket.getCreatedBy().getId());
        }

        @Test
        void submitTicketSetsAllFields() {
            authenticateAsCustomer();
            Ticket ticket = ticketService.submitTicket("Login Issue", "Cannot log in",
                    Category.ACCESS, Priority.HIGH);

            assertEquals("Login Issue", ticket.getTitle());
            assertEquals("Cannot log in", ticket.getDescription());
            assertEquals(Category.ACCESS, ticket.getCategory());
            assertEquals(Priority.HIGH, ticket.getPriority());
        }
    }

    @Nested
    class GetMyTickets {

        @Test
        void returnsOnlyTicketsForLoggedInUser() {
            // Customer creates tickets
            authenticateAsCustomer();
            ticketService.submitTicket("Customer Ticket", "Desc", Category.GENERAL, Priority.LOW);

            // Agent creates a ticket
            authenticateAsAgent();
            ticketService.submitTicket("Agent Ticket", "Desc", Category.GENERAL, Priority.LOW);

            // Customer should only see their own ticket
            authenticateAsCustomer();
            List<Ticket> myTickets = ticketService.getMyTickets(null);
            assertEquals(1, myTickets.size());
            assertEquals("Customer Ticket", myTickets.get(0).getTitle());
        }

        @Test
        void filtersByStatus() {
            authenticateAsCustomer();
            ticketService.submitTicket("Ticket 1", "Desc", Category.GENERAL, Priority.LOW);
            Ticket ticket2 = ticketService.submitTicket("Ticket 2", "Desc", Category.GENERAL, Priority.LOW);

            // Move ticket2 to IN_PROGRESS
            authenticateAsAgent();
            ticketService.changeStatus(ticket2.getId(), Status.IN_PROGRESS);

            authenticateAsCustomer();
            List<Ticket> openOnly = ticketService.getMyTickets(Status.OPEN);
            assertEquals(1, openOnly.size());
            assertEquals("Ticket 1", openOnly.get(0).getTitle());
        }

        @Test
        void returnsAllTicketsWhenNoFilter() {
            authenticateAsCustomer();
            ticketService.submitTicket("Ticket A", "Desc", Category.GENERAL, Priority.LOW);
            ticketService.submitTicket("Ticket B", "Desc", Category.TECHNICAL, Priority.HIGH);

            List<Ticket> all = ticketService.getMyTickets(null);
            assertEquals(2, all.size());
        }
    }

    @Nested
    class ChangeStatus {

        @Test
        void openToInProgressIsValid() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();

            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            assertEquals(Status.IN_PROGRESS, updated.getStatus());
        }

        @Test
        void inProgressToResolvedIsValid() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);

            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.RESOLVED);
            assertEquals(Status.RESOLVED, updated.getStatus());
        }

        @Test
        void resolvedToClosedIsValid() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            ticketService.changeStatus(ticket.getId(), Status.RESOLVED);

            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.CLOSED);
            assertEquals(Status.CLOSED, updated.getStatus());
        }

        @Test
        void resolvedToOpenIsValid() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            ticketService.changeStatus(ticket.getId(), Status.RESOLVED);

            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.OPEN);
            assertEquals(Status.OPEN, updated.getStatus());
        }

        @Test
        void openToResolvedIsRejected() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();

            assertThrows(RuntimeException.class,
                    () -> ticketService.changeStatus(ticket.getId(), Status.RESOLVED));
        }

        @Test
        void openToClosedIsRejected() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();

            assertThrows(RuntimeException.class,
                    () -> ticketService.changeStatus(ticket.getId(), Status.CLOSED));
        }

        @Test
        void closedToAnyStatusIsRejected() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            ticketService.changeStatus(ticket.getId(), Status.RESOLVED);
            ticketService.changeStatus(ticket.getId(), Status.CLOSED);

            assertThrows(RuntimeException.class,
                    () -> ticketService.changeStatus(ticket.getId(), Status.OPEN));
        }

        @Test
        void transitionToInProgressAutoAssignsUnassignedTicket() {
            Ticket ticket = createTicketAsCustomer();
            assertNull(ticket.getAssignedTo());

            authenticateAsAgent();
            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);

            assertNotNull(updated.getAssignedTo());
            assertEquals(agent.getId(), updated.getAssignedTo().getId());
        }

        @Test
        void transitionToInProgressDoesNotReassignAlreadyAssignedTicket() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.assignTicketToMe(ticket.getId());

            // Authenticate as manager and change status
            authenticateAs("manager@test.com", "ADMIN");
            Ticket updated = ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);

            // Should still be assigned to agent, not manager
            assertEquals(agent.getId(), updated.getAssignedTo().getId());
        }
    }

    @Nested
    class AddComment {

        @Test
        void createsCommentWithCorrectAuthor() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();

            Comment comment = ticketService.addComment(ticket.getId(), "Working on this");

            assertNotNull(comment.getId());
            assertEquals("Working on this", comment.getText());
            assertEquals(agent.getId(), comment.getAuthor().getId());
        }

        @Test
        void onClosedTicketThrowsException() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            ticketService.changeStatus(ticket.getId(), Status.RESOLVED);
            ticketService.changeStatus(ticket.getId(), Status.CLOSED);

            assertThrows(RuntimeException.class,
                    () -> ticketService.addComment(ticket.getId(), "This should fail"));
        }

        @Test
        void commentAppearsInTicketDetail() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.addComment(ticket.getId(), "First comment");
            ticketService.addComment(ticket.getId(), "Second comment");

            Ticket detail = ticketService.getTicketDetail(ticket.getId());
            assertEquals(2, detail.getComments().size());
        }
    }

    @Nested
    class AssignTicket {

        @Test
        void assignTicketToMeSetsAssignedTo() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();

            Ticket assigned = ticketService.assignTicketToMe(ticket.getId());

            assertNotNull(assigned.getAssignedTo());
            assertEquals(agent.getId(), assigned.getAssignedTo().getId());
        }
    }

    @Nested
    class DashboardMetrics {

        @Test
        void countByStatusReturnsCorrectCount() {
            authenticateAsCustomer();
            ticketService.submitTicket("T1", "Desc", Category.GENERAL, Priority.LOW);
            ticketService.submitTicket("T2", "Desc", Category.GENERAL, Priority.LOW);

            assertEquals(2, ticketService.countByStatus(Status.OPEN));
            assertEquals(0, ticketService.countByStatus(Status.IN_PROGRESS));
        }

        @Test
        void countResolvedTodayCountsResolvedTickets() {
            Ticket ticket = createTicketAsCustomer();
            authenticateAsAgent();
            ticketService.changeStatus(ticket.getId(), Status.IN_PROGRESS);
            ticketService.changeStatus(ticket.getId(), Status.RESOLVED);

            assertTrue(ticketService.countResolvedToday() >= 1);
        }

        @Test
        void getOpenTicketsByCategoryReturnsCounts() {
            authenticateAsCustomer();
            ticketService.submitTicket("T1", "Desc", Category.TECHNICAL, Priority.LOW);
            ticketService.submitTicket("T2", "Desc", Category.TECHNICAL, Priority.MEDIUM);
            ticketService.submitTicket("T3", "Desc", Category.BILLING, Priority.HIGH);

            Map<Category, Long> byCategory = ticketService.getOpenTicketsByCategory();
            assertEquals(2, byCategory.get(Category.TECHNICAL));
            assertEquals(1, byCategory.get(Category.BILLING));
            assertEquals(0, byCategory.get(Category.GENERAL));
        }

        @Test
        void getOpenTicketsByPriorityReturnsCounts() {
            authenticateAsCustomer();
            ticketService.submitTicket("T1", "Desc", Category.GENERAL, Priority.HIGH);
            ticketService.submitTicket("T2", "Desc", Category.GENERAL, Priority.HIGH);
            ticketService.submitTicket("T3", "Desc", Category.GENERAL, Priority.LOW);

            Map<Priority, Long> byPriority = ticketService.getOpenTicketsByPriority();
            assertEquals(2, byPriority.get(Priority.HIGH));
            assertEquals(1, byPriority.get(Priority.LOW));
            assertEquals(0, byPriority.get(Priority.MEDIUM));
        }
    }
}
