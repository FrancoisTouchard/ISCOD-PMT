package com.iscod.pmt.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.models.ContributorId;
import com.iscod.pmt.models.Project;
import com.iscod.pmt.models.Task;
import com.iscod.pmt.models.TaskAssignment;
import com.iscod.pmt.models.TaskPriority;
import com.iscod.pmt.models.TaskStatus;
import com.iscod.pmt.repositories.ContributorRepository;
import com.iscod.pmt.repositories.ProjectRepository;
import com.iscod.pmt.repositories.TaskRepository;
import com.iscod.pmt.services.TaskService;

@Service
public class TaskServiceImpl implements TaskService {
	
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ContributorRepository contributorRepository;
    
	@Override
	public List<Task> findAll() {
		List<Task> liste = new ArrayList<Task>();
		taskRepository.findAll().forEach(liste::add);
		
		return liste;
	}
    
	@Override
	public List<Task> findTasksByProjectId(UUID projectId) {
		return this.taskRepository.findByProjectId(projectId);
	}

	@Override
	public Task addTask(UUID projectId, String name, String description, LocalDate dueDate, TaskPriority priority, LocalDate endDate) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant"));

        
        Task newTask = new Task();
        newTask.setProject(project);
        newTask.setName(name);
        newTask.setDescription(description);
        newTask.setDueDate(dueDate);
        newTask.setPriority(priority);
        newTask.setEndDate(endDate);

		return taskRepository.save(newTask);
	}

	@Override
	public void deleteTaskById(UUID taskId) {
		if(!taskRepository.existsById(taskId)) {
			throw new ResourceNotFoundException("Tâche introuvable ou inexistante");
		}
		taskRepository.deleteById(taskId);
	}
	
	@Override
	public void assignTaskToUser(UUID taskId, UUID userId, UUID projectId) {
	    Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable ou inexistante"));
	    AppUser user = contributorRepository.findById(new ContributorId(userId, projectId))
        		.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable ou inexistant"))
	            .getUser();
	    Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant"));

	    TaskAssignment assignment = new TaskAssignment(task, user, project);
	    task.getAssignments().add(assignment);
	    taskRepository.save(task);
	}

	@Override
	public Task partialUpdate(UUID taskId, UUID projectId, Map<String, Object> updates) {
	    Task taskToUpdate = taskRepository.findById(taskId)
	        .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));
	    
	    for(String key : updates.keySet()) {
	        Object value = updates.get(key);
	        switch(key) {
	            case "name": {
	                taskToUpdate.setName((String) value);
	                break;
	            }
	            case "description": {
	                taskToUpdate.setDescription((String) value);
	                break;
	            }
	            case "dueDate": {
	                if (value instanceof String) {
	                    taskToUpdate.setDueDate(LocalDate.parse((String) value));
	                } else {
	                    taskToUpdate.setDueDate((LocalDate) value);
	                }
	                break;
	            }
	            case "endDate": {
	                if (value == null) {
	                    taskToUpdate.setEndDate(null);
	                } else if (value instanceof String) {
	                    taskToUpdate.setEndDate(LocalDate.parse((String) value));
	                } else {
	                    taskToUpdate.setEndDate((LocalDate) value);
	                }
	                break;
	            }
	            case "priority": {
	                if (value instanceof String) {
	                    taskToUpdate.setPriority(TaskPriority.valueOf((String) value));
	                } else {
	                    taskToUpdate.setPriority((TaskPriority) value);
	                }
	                break;
	            }
	            case "status": {
	                if (value instanceof String) {
	                    taskToUpdate.setStatus(TaskStatus.valueOf((String) value));
	                } else {
	                    taskToUpdate.setStatus((TaskStatus) value);
	                }
	                break;
	            }
	            case "assigneeIds": {
	                taskToUpdate.getAssignments().clear();
	                
	                List<String> assigneeIds = ((List<?>) value)
	                    .stream()
	                    .map(Object::toString)
	                    .toList();
	                                
	                for(String idStr : assigneeIds) {
	                    UUID userId = UUID.fromString(idStr);
	                    AppUser user = contributorRepository.findById(new ContributorId(userId, projectId))
	                        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable: " + userId))
	                        .getUser();
	                    
	                    TaskAssignment assignment = new TaskAssignment(taskToUpdate, user, taskToUpdate.getProject());
	                    taskToUpdate.getAssignments().add(assignment);
	                }
	                break;
	            }
	        }
	    }
	    
	    return taskRepository.save(taskToUpdate);
	}

}
