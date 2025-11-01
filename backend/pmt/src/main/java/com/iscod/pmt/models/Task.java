package com.iscod.pmt.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "task")
@Entity
public class Task {
	
	@Id
	@GeneratedValue
	private UUID id;
    
	@NotNull(message="Une tâche doit avoir un nom.")
    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
	@NotNull(message="Une tâche doit avoir une date d'échéance.")
    private LocalDate dueDate;

    private LocalDate endDate;
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference("project")
    private Project project;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("task-assignments")
    private Set<TaskAssignment> assignments = new HashSet<>();
    
    public Task() {}
    
    public Task(Project project, String name, String description, LocalDate dueDate, TaskPriority priority) {
    	this.project = project;
    	this.name = name;
    	this.description = description;
    	this.dueDate = dueDate;
    	this.priority = priority;
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TaskPriority getPriority() {
		return priority;
	}

	public void setPriority(TaskPriority priority) {
		this.priority = priority;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<TaskAssignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(Set<TaskAssignment> assignments) {
		this.assignments = assignments;
	}

}
