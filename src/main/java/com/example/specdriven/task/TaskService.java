package com.example.specdriven.task;

import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskDependencyRepository taskDependencyRepository,
                       ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
        this.projectRepository = projectRepository;
    }

    public List<Task> findByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task save(Task task) {
        validate(task);
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long taskId) {
        taskDependencyRepository.deleteByPredecessorTaskIdOrSuccessorTaskId(taskId, taskId);
        taskRepository.deleteById(taskId);
    }

    private void validate(Task task) {
        if (task.getName() == null || task.getName().isBlank()) {
            throw new IllegalArgumentException("Task name is required");
        }
        if (task.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (task.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (task.getEndDate().isBefore(task.getStartDate())) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        Project project = task.getProject();
        if (project != null) {
            if (task.getStartDate().isBefore(project.getStartDate())) {
                throw new IllegalArgumentException("Task start date must not be before project start date");
            }
            if (task.getEndDate().isAfter(project.getEndDate())) {
                throw new IllegalArgumentException("Task end date must not be after project end date");
            }
        }
    }
}
