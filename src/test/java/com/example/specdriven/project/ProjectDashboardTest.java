package com.example.specdriven.project;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectDashboardTest extends SpringBrowserlessTest {

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboardShowsProjectNames() {
        navigate(ProjectDashboardView.class);
        var names = $(H3.class).all().stream().map(H3::getText).toList();
        assertTrue(names.contains("Website Redesign"));
        assertTrue(names.contains("Mobile App MVP"));
        assertTrue(names.contains("API Integration"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboardShowsProgressBars() {
        navigate(ProjectDashboardView.class);
        assertTrue($(ProgressBar.class).exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSeesNewProjectButton() {
        navigate(ProjectDashboardView.class);
        assertTrue($(Button.class).withText("New Project").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminDoesNotSeeNewProjectButton() {
        navigate(ProjectDashboardView.class);
        assertFalse($(Button.class).withText("New Project").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProjectDialogOpens() {
        ProjectDashboardView view = navigate(ProjectDashboardView.class);
        view.openCreateDialog();
        assertTrue($(Dialog.class).exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboardShowsStatusBadges() {
        navigate(ProjectDashboardView.class);
        var spans = $(Span.class).all();
        boolean hasInProgress = spans.stream().anyMatch(s -> "IN PROGRESS".equals(s.getText()));
        boolean hasNotStarted = spans.stream().anyMatch(s -> "NOT STARTED".equals(s.getText()));
        assertTrue(hasInProgress);
        assertTrue(hasNotStarted);
    }
}
