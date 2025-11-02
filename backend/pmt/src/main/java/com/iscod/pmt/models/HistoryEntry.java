package com.iscod.pmt.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "history")
@Entity
public class HistoryEntry {

	@Id
	@GeneratedValue
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "id_project")
	@JsonBackReference("project-history")
	@NotNull(message = "Le projet est obligatoire")
	private Project project;

	@ManyToOne
	@JoinColumn(name = "id_task")
	@JsonBackReference("task-history")
	@NotNull(message = "La tâche est obligatoire")
	private Task task;

	@ManyToOne
	@JoinColumn(name = "id_user")
	@JsonBackReference("user-history")
	@NotNull(message = "L'utilisateur est obligatoire")
	private AppUser user;

	@NotNull(message = "Le nom de l'utilisateur est obligatoire")
	private String userName;

	@NotNull(message = "Le nom de la tâche est obligatoire")
	private String taskName;

	@NotNull(message = "Le nom du champ modifié est obligatoire")
	private String modifiedFieldName;

	private String oldFieldValue;

	private String newFieldValue;

	private LocalDateTime modifiedAt;

	public HistoryEntry() {
	}

	public HistoryEntry(Project project, Task task, AppUser user, String modifiedFieldName, String oldFieldValue,
			String newFieldValue) {
		this.project = project;
		this.task = task;
		this.user = user;
		this.userName = user.getName();
		this.taskName = task.getName();
		this.modifiedFieldName = modifiedFieldName;
		this.oldFieldValue = oldFieldValue;
		this.newFieldValue = newFieldValue;
		this.modifiedAt = LocalDateTime.now();
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getModifiedFieldName() {
		return modifiedFieldName;
	}

	public void setModifiedFieldName(String modifiedFieldName) {
		this.modifiedFieldName = modifiedFieldName;
	}

	public String getOldFieldValue() {
		return oldFieldValue;
	}

	public void setOldFieldValue(String oldFieldValue) {
		this.oldFieldValue = oldFieldValue;
	}

	public String getNewFieldValue() {
		return newFieldValue;
	}

	public void setNewFieldValue(String newFieldValue) {
		this.newFieldValue = newFieldValue;
	}

	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

}
