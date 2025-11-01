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
import com.iscod.pmt.models.TaskStatus;
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
	public Task addTask(@PathVariable UUID projectId, @RequestBody Map<String, Object> taskData) {
	    
			// champs obligatoires
	    	String name = (String) taskData.get("name");
	        LocalDate dueDate = LocalDate.parse((String) taskData.get("dueDate"));
	        TaskPriority priority = TaskPriority.valueOf((String) taskData.get("priority"));
	        TaskStatus status = TaskStatus.valueOf((String) taskData.get("status"));

	        // champs optionnels
	        String description = null;
	        if(taskData.containsKey("description") && taskData.get("description") != null) {
	        	description = (String) taskData.get("description");
	        }
	        LocalDate endDate = null;
	        if (taskData.containsKey("endDate") && taskData.get("endDate") != null) {
	            endDate = LocalDate.parse((String) taskData.get("endDate"));
	        }
	        
	        Task task = taskService.addTask(projectId, name, description, dueDate, priority, endDate, status);

	        if(taskData.containsKey("assigneeIds")) {
	            List<String> assigneeIds = ((List<?>) taskData.get("assigneeIds"))
	                .stream()
	                .map(Object::toString)
	                .toList();
	            for(String idStr : assigneeIds) {
	                UUID userId = UUID.fromString(idStr);
	                taskService.assignTaskToUser(task.getId(), userId, projectId);
	            }
	        }

	    	return task;
	}
	
	@PatchMapping("/project/{projectId}/{taskId}")
	@ResponseStatus(code = HttpStatus.OK)
	public Task partialUpdate(
	    @PathVariable UUID projectId,
	    @PathVariable UUID taskId, 
	    @RequestBody Map<String, Object> updates
	) {
	    return taskService.partialUpdate(taskId, projectId, updates);
	}
	   
	@DeleteMapping("/{taskId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID taskId) {
		taskService.deleteTaskById(taskId);
	}

}
