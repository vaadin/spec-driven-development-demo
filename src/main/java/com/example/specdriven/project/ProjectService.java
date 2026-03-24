package com.example.specdriven.project;

import com.example.specdriven.task.TaskDependencyRepository;
import com.example.specdriven.task.TaskRepository;
import com.example.specdriven.task.TaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TaskDependencyRepository taskDependencyRepository;

    public ProjectService(ProjectRepository projectRepository,
                          TaskRepository taskRepository,
                          TaskDependencyRepository taskDependencyRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.taskDependencyRepository = taskDependencyRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public Project save(Project project) {
        validate(project);
        return projectRepository.save(project);
    }

    @Transactional
    public void delete(Long projectId) {
        var tasks = taskRepository.findByProjectId(projectId);
        for (var task : tasks) {
            taskDependencyRepository.deleteByPredecessorTaskIdOrSuccessorTaskId(task.getId(), task.getId());
        }
        taskRepository.deleteAll(tasks);
        projectRepository.deleteById(projectId);
    }

    public ProjectStatus computeStatus(Long projectId) {
        long total = taskRepository.countByProjectId(projectId);
        if (total == 0) {
            return ProjectStatus.NOT_STARTED;
        }
        long doneCount = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        if (doneCount == total) {
            return ProjectStatus.COMPLETED;
        }
        long todoCount = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.TODO);
        if (todoCount == total) {
            return ProjectStatus.NOT_STARTED;
        }
        return ProjectStatus.IN_PROGRESS;
    }

    public int computeProgressPercent(Long projectId) {
        long total = taskRepository.countByProjectId(projectId);
        if (total == 0) {
            return 0;
        }
        long doneCount = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.DONE);
        return (int) (doneCount * 100 / total);
    }

    private void validate(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        if (project.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (project.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (!project.getEndDate().isAfter(project.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}
