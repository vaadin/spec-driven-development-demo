package com.example.specdriven.task;

import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskRepository taskRepository;

    private Project getWebsiteProject() {
        return projectService.findAll().stream()
                .filter(p -> p.getName().equals("Website Redesign"))
                .findFirst().orElseThrow();
    }

    @Test
    void findByProjectIdReturnsTasks() {
        Project project = getWebsiteProject();
        List<Task> tasks = taskService.findByProjectId(project.getId());
        assertTrue(tasks.size() >= 4, "Expected at least 4 tasks from demo data");
    }

    @Test
    void createTaskWithValidData() {
        Project project = getWebsiteProject();
        Task task = new Task("New Task", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), project);
        Task saved = taskService.save(task);
        assertNotNull(saved.getId());
        assertEquals("New Task", saved.getName());
        assertEquals(TaskStatus.TODO, saved.getStatus());
        assertEquals(TaskPriority.MEDIUM, saved.getPriority());
    }

    @Test
    void createTaskRequiresName() {
        Project project = getWebsiteProject();
        Task task = new Task("", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 10), project);
        assertThrows(IllegalArgumentException.class, () -> taskService.save(task));
    }

    @Test
    void taskEndDateMustBeOnOrAfterStartDate() {
        Project project = getWebsiteProject();
        Task task = new Task("Bad Dates", LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 1), project);
        assertThrows(IllegalArgumentException.class, () -> taskService.save(task));
    }

    @Test
    void taskStartDateMustNotBeBeforeProjectStart() {
        Project project = getWebsiteProject();
        Task task = new Task("Early Start", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 3, 10), project);
        assertThrows(IllegalArgumentException.class, () -> taskService.save(task));
    }

    @Test
    void taskEndDateMustNotBeAfterProjectEnd() {
        Project project = getWebsiteProject();
        Task task = new Task("Late End", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 31), project);
        assertThrows(IllegalArgumentException.class, () -> taskService.save(task));
    }

    @Test
    void taskSameDayStartEndIsValid() {
        Project project = getWebsiteProject();
        Task task = new Task("One Day", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 1), project);
        Task saved = taskService.save(task);
        assertNotNull(saved.getId());
    }

    @Test
    void deleteTaskRemovesTask() {
        Project project = getWebsiteProject();
        Task task = taskService.save(new Task("To Delete", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 5), project));
        Long id = task.getId();
        taskService.delete(id);
        assertNull(taskService.findById(id));
    }
}
