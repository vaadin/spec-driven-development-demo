package com.example.specdriven.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {
    List<TaskDependency> findByPredecessorTaskId(Long taskId);
    List<TaskDependency> findBySuccessorTaskId(Long taskId);
    List<TaskDependency> findByPredecessorTaskProjectId(Long projectId);
    void deleteByPredecessorTaskIdOrSuccessorTaskId(Long predecessorId, Long successorId);
}
