package com.iscod.pmt.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.iscod.pmt.services.EmailService;
import com.iscod.pmt.services.HistoryEntryService;
import com.iscod.pmt.services.TaskService;
import com.iscod.pmt.utils.FieldFormatterUtils;

@Service
public class TaskServiceImpl implements TaskService {
	
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ContributorRepository contributorRepository;
    
    @Autowired
    private HistoryEntryService historyEntryService;
    
    @Autowired
    private EmailService emailService;

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
	public Task addTask(UUID projectId, String name, String description, LocalDate dueDate, TaskPriority priority, LocalDate endDate, TaskStatus status) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant"));
        
        Task newTask = new Task();
        newTask.setProject(project);
        newTask.setName(name);
        newTask.setDescription(description);
        newTask.setDueDate(dueDate);
        newTask.setPriority(priority);
        newTask.setEndDate(endDate);
        newTask.setStatus(status);

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
	    AppUser assignee = contributorRepository.findById(new ContributorId(userId, projectId))
        		.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable ou inexistant"))
	            .getUser();
	    Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant"));

	    TaskAssignment assignment = new TaskAssignment(task, assignee, project);
	    task.getAssignments().add(assignment);
	    taskRepository.save(task);
	    
	 // Envoi de la notification par email
	 emailService.sendTaskAssignmentNotification(task, assignee);
	}

	@Override
	@Transactional
	public Task partialUpdate(UUID taskId, UUID projectId, UUID currentUserId, Map<String, Object> updates) {
	    Task taskToUpdate = taskRepository.findById(taskId)
	        .orElseThrow(() -> new ResourceNotFoundException("Tâche introuvable"));
	    
	    // Récupérer l'auteur de la modification pour l'entrée d'historique
	    AppUser historyEntryAuthor = contributorRepository.findById(new ContributorId(currentUserId, projectId))
		        .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"))
		        .getUser();
	    
	    for(String key : updates.keySet()) {
	        Object value = updates.get(key);
	        
	        // récupérer l'ancienne valeur du champ pour l'entrée d'historique
	        String oldValue = FieldFormatterUtils.getFieldValue(taskToUpdate, key);
	        String newValueStr = FieldFormatterUtils.convertValueToString(key, value);
	        
	        // Garder une trace des anciennes assignations pour savoir qui notifier
		    Set<UUID> oldAssigneeIds = new HashSet<>();
		    taskToUpdate.getAssignments().forEach(a -> oldAssigneeIds.add(a.getUser().getId()));
		    
	        
	        // ne rien faire si la valeur ne change pas
	        if (oldValue.equals(newValueStr)) {
	            continue;
	        }
	        
	        switch(key) {
	            case "name": {
	                taskToUpdate.setName((String) value);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, (String) value);
	                break;
	            }
	            case "description": {
	                taskToUpdate.setDescription((String) value);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, (String) value);
	                break;
	            }
	            case "dueDate": {
	                LocalDate newDate = value instanceof String ? LocalDate.parse((String) value) : (LocalDate) value;
	                taskToUpdate.setDueDate(newDate);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, newDate.toString());
	                break;
	            }
	            case "endDate": {
	                LocalDate newDate = value == null ? null : 
	                    (value instanceof String ? LocalDate.parse((String) value) : (LocalDate) value);
	                taskToUpdate.setEndDate(newDate);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, 
	                    newDate != null ? newDate.toString() : "");
	                break;
	            }
	            case "priority": {
	                TaskPriority newPriority = value instanceof String ? 
	                    TaskPriority.valueOf((String) value) : (TaskPriority) value;
	                taskToUpdate.setPriority(newPriority);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, newPriority.toString());
	                break;
	            }
	            case "status": {
	                TaskStatus newStatus = value instanceof String ? 
	                    TaskStatus.valueOf((String) value) : (TaskStatus) value;
	                taskToUpdate.setStatus(newStatus);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, newStatus.toString());
	                break;
	            }
	            case "assigneeIds": {
	                // vider les anciennes assignations
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
	                    
	                    // Envoi de la notification aux nouveaux assignés
	                    if (!oldAssigneeIds.contains(userId)) {
	                       emailService.sendTaskAssignmentNotification(taskToUpdate, user);
	                    }
	                }
	                
	                String newAssignees = assigneeIds.isEmpty() ? "(aucun)" : String.join(", ", assigneeIds);
	                this.historyEntryService.createHistoryEntry(taskToUpdate, historyEntryAuthor, key, oldValue, newAssignees);
	                break;
	            }
	        }
	    }
	    
	    return taskRepository.save(taskToUpdate);
	}

}
