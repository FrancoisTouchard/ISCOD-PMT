package com.iscod.pmt.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable
public class TaskAssignmentId implements Serializable {
    
    private static final long serialVersionUID = 1L;
	private UUID taskId;
    private UUID userId;
    private UUID projectId;

    public TaskAssignmentId() {}

    public TaskAssignmentId(UUID taskId, UUID userId, UUID projectId) {
        this.taskId = taskId;
        this.userId = userId;
        this.projectId = projectId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskAssignmentId)) return false;
        TaskAssignmentId that = (TaskAssignmentId) o;
        return Objects.equals(taskId, that.taskId)
            && Objects.equals(userId, that.userId)
            && Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId, projectId);
    }
}
