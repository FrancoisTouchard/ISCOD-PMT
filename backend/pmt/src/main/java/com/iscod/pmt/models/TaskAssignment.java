package com.iscod.pmt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "task_assignment")
public class TaskAssignment {

    @EmbeddedId
    private TaskAssignmentId id;

    @ManyToOne
    @MapsId("taskId")
    @JoinColumn(name = "id_task")
    @JsonBackReference("task-assignments")
    private Task task;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    @JsonBackReference("user-assignments")
    private AppUser user;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "id_project")
    @JsonBackReference("project-assignments")
    private Project project;

    public TaskAssignment() {}

    public TaskAssignment(Task task, AppUser user, Project project) {
        this.id = new TaskAssignmentId(task.getId(), user.getId(), project.getId());
        this.task = task;
        this.user = user;
        this.project = project;
    }

    public TaskAssignmentId getId() {
        return id;
    }

    public void setId(TaskAssignmentId id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
