package com.example.specdriven.admin.ticket;

import com.example.specdriven.model.*;
import com.example.specdriven.repository.CommentRepository;
import com.example.specdriven.repository.TicketRepository;
import com.example.specdriven.repository.UserRepository;
import com.example.specdriven.service.TicketService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
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
class WorkOnTicketTest extends SpringBrowserlessTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        ticketRepository.deleteAll();

        // Create a ticket as customer
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("customer@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))));

        testTicket = ticketService.submitTicket("Test Issue", "Something is broken",
                Category.TECHNICAL, Priority.HIGH);

        // Restore agent auth for tests
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("agent@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    void ticketDetailViewRendersTicketTitle() {
        navigate("admin/ticket/" + testTicket.getId(), TicketDetailView.class);

        H1 heading = $(H1.class).first();
        assertNotNull(heading);
        assertTrue(heading.getText().contains("Test Issue"),
                "Title should contain the ticket title");
    }

    @Test
    void ticketDetailViewShowsStatusChangeSelect() {
        navigate("admin/ticket/" + testTicket.getId(), TicketDetailView.class);

        Select statusSelect = $(Select.class).first();
        assertNotNull(statusSelect, "Status change select should be present for open tickets");
    }

    @Test
    void ticketDetailViewShowsCommentInput() {
        navigate("admin/ticket/" + testTicket.getId(), TicketDetailView.class);

        TextArea commentInput = $(TextArea.class).first();
        assertNotNull(commentInput, "Comment input should be present for non-closed tickets");
    }

    @Test
    void ticketDetailShowsTicketMetadata() {
        navigate("admin/ticket/" + testTicket.getId(), TicketDetailView.class);

        // The view should render without errors and show the detail section
        H1 heading = $(H1.class).first();
        assertNotNull(heading);
        assertTrue(heading.getText().contains("#" + testTicket.getId()),
                "Title should contain the ticket ID");
    }

    @Test
    @WithAnonymousUser
    void anonymousUserCannotAccessTicketDetail() {
        assertThrows(Exception.class,
                () -> navigate("admin/ticket/" + testTicket.getId(), TicketDetailView.class),
                "Anonymous user should not be able to access ticket detail");
    }
}
