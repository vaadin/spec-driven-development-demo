package com.example.specdriven.admin.queue;

import com.example.specdriven.model.*;
import com.example.specdriven.repository.CommentRepository;
import com.example.specdriven.repository.TicketRepository;
import com.example.specdriven.repository.UserRepository;
import com.example.specdriven.service.TicketService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.select.Select;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(username = "agent@test.com", roles = "ADMIN")
class ManageTicketQueueTest extends SpringBrowserlessTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();

        // Create test tickets as customer
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("customer@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        ticketService.submitTicket("Login Issue", "Cannot log in", Category.ACCESS, Priority.HIGH);
        ticketService.submitTicket("Billing Question", "Overcharged", Category.BILLING, Priority.MEDIUM);
        ticketService.submitTicket("Server Down", "Production is down", Category.TECHNICAL, Priority.CRITICAL);

        // Restore agent auth for tests
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("agent@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    void gridRendersWithTickets() {
        TicketQueueView view = navigate(TicketQueueView.class);

        Grid<Ticket> grid = $(Grid.class).first();
        assertNotNull(grid);
        assertTrue(grid.getGenericDataView().getItems().count() > 0,
                "Grid should contain tickets");
    }

    @Test
    void gridHasExpectedColumns() {
        TicketQueueView view = navigate(TicketQueueView.class);

        Grid<Ticket> grid = $(Grid.class).first();
        List<String> headers = grid.getColumns().stream()
                .map(col -> col.getHeaderText())
                .filter(h -> h != null && !h.isEmpty())
                .toList();

        assertTrue(headers.contains("ID"), "Grid should have ID column");
        assertTrue(headers.contains("Title"), "Grid should have Title column");
        assertTrue(headers.contains("Category"), "Grid should have Category column");
        assertTrue(headers.contains("Created By"), "Grid should have Created By column");
        assertTrue(headers.contains("Assigned To"), "Grid should have Assigned To column");
    }

    @Test
    void gridShowsAllOpenTicketsByDefault() {
        TicketQueueView view = navigate(TicketQueueView.class);

        Grid<Ticket> grid = $(Grid.class).first();
        long count = grid.getGenericDataView().getItems().count();
        assertEquals(3, count, "All 3 OPEN tickets should be displayed by default");
    }

    @Test
    void viewHasFilterControls() {
        TicketQueueView view = navigate(TicketQueueView.class);

        List<Select> selects = $(Select.class).all();
        assertTrue(selects.size() >= 3, "View should have at least 3 filter selects (status, category, priority)");
    }

    @Test
    void pageHasTitle() {
        TicketQueueView view = navigate(TicketQueueView.class);

        H1 heading = $(H1.class).first();
        assertNotNull(heading);
        assertEquals("Ticket Queue", heading.getText());
    }

    @Test
    @WithAnonymousUser
    void anonymousUserCannotAccessQueue() {
        assertThrows(Exception.class, () -> navigate(TicketQueueView.class),
                "Anonymous user should not be able to access the ticket queue");
    }
}
