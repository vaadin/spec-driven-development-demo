package com.example.specdriven.gantt;

import com.example.specdriven.project.ProjectService;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GanttChartTest extends SpringBrowserlessTest {

    @Autowired
    private ProjectService projectService;

    private Long getWebsiteProjectId() {
        return projectService.findAll().stream()
                .filter(p -> p.getName().equals("Website Redesign"))
                .findFirst().orElseThrow().getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ganttChartShowsProjectTitle() {
        Long projectId = getWebsiteProjectId();
        navigate(GanttChartView.class, Collections.singletonMap("projectId", projectId.toString()));
        var heading = $(H2.class).single();
        assertTrue(heading.getText().contains("Gantt Chart"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ganttChartHasZoomButtons() {
        Long projectId = getWebsiteProjectId();
        navigate(GanttChartView.class, Collections.singletonMap("projectId", projectId.toString()));
        assertTrue($(Button.class).withText("Day").exists());
        assertTrue($(Button.class).withText("Week").exists());
        assertTrue($(Button.class).withText("Month").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ganttChartHasBackToTasksButton() {
        Long projectId = getWebsiteProjectId();
        navigate(GanttChartView.class, Collections.singletonMap("projectId", projectId.toString()));
        assertTrue($(Button.class).withText("Back to Tasks").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ganttChartRendersTaskBars() {
        Long projectId = getWebsiteProjectId();
        navigate(GanttChartView.class, Collections.singletonMap("projectId", projectId.toString()));
        // Task bars contain Span elements with task names
        var spans = $(Span.class).all();
        boolean hasDesignWireframes = spans.stream().anyMatch(s -> "Design wireframes".equals(s.getText()));
        assertTrue(hasDesignWireframes, "Should render task bar labels");
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminCanViewGanttChart() {
        Long projectId = getWebsiteProjectId();
        navigate(GanttChartView.class, Collections.singletonMap("projectId", projectId.toString()));
        var heading = $(H2.class).single();
        assertTrue(heading.getText().contains("Gantt Chart"));
    }
}
