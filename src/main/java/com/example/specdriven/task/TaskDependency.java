package com.example.specdriven.task;

import jakarta.persistence.*;

@Entity
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "predecessor_task_id")
    private Task predecessorTask;

    @ManyToOne(optional = false)
    @JoinColumn(name = "successor_task_id")
    private Task successorTask;

    public TaskDependency() {
    }

    public TaskDependency(Task predecessorTask, Task successorTask) {
        this.predecessorTask = predecessorTask;
        this.successorTask = successorTask;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getPredecessorTask() {
        return predecessorTask;
    }

    public void setPredecessorTask(Task predecessorTask) {
        this.predecessorTask = predecessorTask;
    }

    public Task getSuccessorTask() {
        return successorTask;
    }

    public void setSuccessorTask(Task successorTask) {
        this.successorTask = successorTask;
    }
}
