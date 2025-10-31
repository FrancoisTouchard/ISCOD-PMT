package com.iscod.pmt.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.iscod.pmt.models.Task;
import com.iscod.pmt.models.TaskPriority;
import com.iscod.pmt.services.TaskService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/tasks")
public class TaskController {
	
	@Autowired
	private TaskService taskService;
	
	@GetMapping
	public List<Task> findAll(){
		return taskService.findAll();
	}

	@GetMapping("/project/{projectId}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Task> getTasksByProjectId(@PathVariable UUID projectId) {
		return taskService.findTasksByProjectId(projectId);
	}
	   
	@PostMapping("/project/{projectId}")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Task addTask(@PathVariable UUID projectId, @RequestBody Map<String, String> taskData) {
	    
	    	String name = taskData.get("name");
	    	String description = taskData.get("description");
	    	String dueDateStr = taskData.get("dueDate");
	    	LocalDate dueDate = LocalDate.parse(dueDateStr);
	    	String priorityStr = taskData.get("priority");
	    	TaskPriority priority = TaskPriority.valueOf(priorityStr);
	    	
	    	return taskService.addTask(projectId, name, description, dueDate, priority);
	}
	    
	@DeleteMapping("/{taskId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID taskId) {
		taskService.deleteTaskById(taskId);
	}

}
