package com.example.specdriven.task;

import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectService;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskManagementTest extends SpringBrowserlessTest {

    @Autowired
    private ProjectService projectService;

    private Long getWebsiteProjectId() {
        return projectService.findAll().stream()
                .filter(p -> p.getName().equals("Website Redesign"))
                .findFirst().orElseThrow().getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void taskViewShowsProjectTitle() {
        Long projectId = getWebsiteProjectId();
        navigate(TaskManagementView.class, Collections.singletonMap("projectId", projectId.toString()));
        var heading = $(H2.class).single();
        assertTrue(heading.getText().contains("Website Redesign"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void taskGridShowsTasks() {
        Long projectId = getWebsiteProjectId();
        TaskManagementView view = navigate(TaskManagementView.class,
                Collections.singletonMap("projectId", projectId.toString()));
        assertTrue($(Grid.class).exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSeesAddTaskButton() {
        Long projectId = getWebsiteProjectId();
        navigate(TaskManagementView.class, Collections.singletonMap("projectId", projectId.toString()));
        assertTrue($(Button.class).withText("Add Task").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void nonAdminDoesNotSeeAddTaskButton() {
        Long projectId = getWebsiteProjectId();
        navigate(TaskManagementView.class, Collections.singletonMap("projectId", projectId.toString()));
        assertFalse($(Button.class).withText("Add Task").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addTaskDialogOpens() {
        Long projectId = getWebsiteProjectId();
        TaskManagementView view = navigate(TaskManagementView.class,
                Collections.singletonMap("projectId", projectId.toString()));
        view.openTaskDialog(null);
        assertTrue($(Dialog.class).exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ganttChartButtonExists() {
        Long projectId = getWebsiteProjectId();
        navigate(TaskManagementView.class, Collections.singletonMap("projectId", projectId.toString()));
        assertTrue($(Button.class).withText("Gantt Chart").exists());
    }
}
