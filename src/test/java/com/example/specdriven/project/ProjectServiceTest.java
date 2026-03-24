package com.example.specdriven.project;

import com.example.specdriven.data.DataInitializer;
import com.example.specdriven.task.TaskRepository;
import com.example.specdriven.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findAllReturnsAllProjects() {
        List<Project> projects = projectService.findAll();
        assertFalse(projects.isEmpty());
    }

    @Test
    void createProjectWithValidData() {
        Project project = new Project("Test Project", "Description",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31));
        Project saved = projectService.save(project);
        assertNotNull(saved.getId());
        assertEquals("Test Project", saved.getName());
    }

    @Test
    void createProjectRequiresName() {
        Project project = new Project("", "Description",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31));
        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void createProjectRequiresStartDate() {
        Project project = new Project("Name", "Description", null, LocalDate.of(2026, 12, 31));
        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void createProjectRequiresEndDate() {
        Project project = new Project("Name", "Description", LocalDate.of(2026, 6, 1), null);
        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void endDateMustBeAfterStartDate() {
        Project project = new Project("Name", "Description",
                LocalDate.of(2026, 12, 31), LocalDate.of(2026, 6, 1));
        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void endDateCannotEqualStartDate() {
        Project project = new Project("Name", "Description",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1));
        assertThrows(IllegalArgumentException.class, () -> projectService.save(project));
    }

    @Test
    void deleteProjectRemovesProjectAndTasks() {
        Project project = projectService.save(new Project("To Delete", null,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)));
        Long id = project.getId();

        projectService.delete(id);

        assertNull(projectService.findById(id));
        assertTrue(taskRepository.findByProjectId(id).isEmpty());
    }

    @Test
    void computeStatusNotStartedWhenNoTasks() {
        // API Integration project has no tasks
        Project apiProject = projectService.findAll().stream()
                .filter(p -> p.getName().equals("API Integration"))
                .findFirst().orElseThrow();

        assertEquals(ProjectStatus.NOT_STARTED, projectService.computeStatus(apiProject.getId()));
    }

    @Test
    void computeStatusInProgressWhenMixedTasks() {
        // Website Redesign has DONE, IN_PROGRESS, and TODO tasks
        Project webProject = projectService.findAll().stream()
                .filter(p -> p.getName().equals("Website Redesign"))
                .findFirst().orElseThrow();

        assertEquals(ProjectStatus.IN_PROGRESS, projectService.computeStatus(webProject.getId()));
    }

    @Test
    void computeProgressPercentCorrectly() {
        // Website Redesign: 1 DONE out of 4 tasks = 25%
        Project webProject = projectService.findAll().stream()
                .filter(p -> p.getName().equals("Website Redesign"))
                .findFirst().orElseThrow();

        assertEquals(25, projectService.computeProgressPercent(webProject.getId()));
    }

    @Test
    void computeProgressZeroWhenNoTasks() {
        Project apiProject = projectService.findAll().stream()
                .filter(p -> p.getName().equals("API Integration"))
                .findFirst().orElseThrow();

        assertEquals(0, projectService.computeProgressPercent(apiProject.getId()));
    }
}
