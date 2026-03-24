package com.example.specdriven.workload;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamWorkloadTest extends SpringBrowserlessTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void workloadViewShowsHeading() {
        navigate(TeamWorkloadView.class);
        var heading = $(H2.class).single();
        assertEquals("Team Workload", heading.getText());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void workloadViewShowsGrid() {
        navigate(TeamWorkloadView.class);
        assertTrue($(Grid.class).exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminCanAccessWorkloadView() {
        navigate(TeamWorkloadView.class);
        assertTrue($(H2.class).exists());
    }
}
