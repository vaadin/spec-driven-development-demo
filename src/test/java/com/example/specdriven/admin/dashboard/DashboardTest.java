package com.example.specdriven.admin.dashboard;

import com.example.specdriven.model.*;
import com.example.specdriven.repository.CommentRepository;
import com.example.specdriven.repository.TicketRepository;
import com.example.specdriven.repository.UserRepository;
import com.example.specdriven.service.TicketService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
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
class DashboardTest extends SpringBrowserlessTest {

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

        ticketService.submitTicket("Ticket 1", "Desc", Category.TECHNICAL, Priority.HIGH);
        ticketService.submitTicket("Ticket 2", "Desc", Category.BILLING, Priority.MEDIUM);
        ticketService.submitTicket("Ticket 3", "Desc", Category.TECHNICAL, Priority.LOW);

        // Restore agent auth
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("agent@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    void dashboardRendersWithTitle() {
        DashboardView view = navigate(DashboardView.class);

        H1 heading = $(H1.class).first();
        assertNotNull(heading);
        assertEquals("Dashboard", heading.getText());
    }

    @Test
    void dashboardShowsStatCards() {
        DashboardView view = navigate(DashboardView.class);

        // Stat cards container should exist with class "stat-cards"
        List<Div> divs = $(Div.class).all();
        boolean hasStatCards = divs.stream()
                .anyMatch(d -> d.getClassNames().contains("stat-cards"));
        assertTrue(hasStatCards, "Dashboard should contain stat cards section");
    }

    @Test
    void dashboardShowsBreakdownGrids() {
        DashboardView view = navigate(DashboardView.class);

        List<Grid> grids = $(Grid.class).all();
        assertEquals(2, grids.size(), "Dashboard should have 2 breakdown grids (category and priority)");
    }

    @Test
    void dashboardShowsBreakdownHeadings() {
        DashboardView view = navigate(DashboardView.class);

        List<H3> headings = $(H3.class).all();
        List<String> headingTexts = headings.stream().map(H3::getText).toList();

        assertTrue(headingTexts.contains("Tickets by Category"),
                "Dashboard should have 'Tickets by Category' heading");
        assertTrue(headingTexts.contains("Tickets by Priority"),
                "Dashboard should have 'Tickets by Priority' heading");
    }

    @Test
    @WithAnonymousUser
    void anonymousUserCannotAccessDashboard() {
        assertThrows(Exception.class, () -> navigate(DashboardView.class),
                "Anonymous user should not be able to access the dashboard");
    }
}
