package com.iscod.pmt.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.iscod.pmt.models.Task;
import com.iscod.pmt.models.TaskPriority;

public interface TaskService {
	
	List<Task> findAll();

	List<Task> findTasksByProjectId(UUID projectId);

	Task addTask(UUID projectId, String name, String description, LocalDate dueDate, TaskPriority priority, LocalDate endDate);

	void deleteTaskById(UUID taskId);
	
	void assignTaskToUser(UUID taskId, UUID userId, UUID projectId);

}
