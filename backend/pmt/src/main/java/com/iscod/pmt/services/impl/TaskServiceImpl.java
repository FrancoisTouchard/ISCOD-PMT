package com.iscod.pmt.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.Project;
import com.iscod.pmt.models.Task;
import com.iscod.pmt.models.TaskPriority;
import com.iscod.pmt.repositories.ProjectRepository;
import com.iscod.pmt.repositories.TaskRepository;
import com.iscod.pmt.services.TaskService;

@Service
public class TaskServiceImpl implements TaskService {
	
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
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
	public Task addTask(UUID projectId, String name, String description, LocalDate dueDate, TaskPriority priority) {
        Project project = projectRepository.findById(projectId).orElseThrow(ResourceNotFoundException::new);
        
        Task newTask = new Task();
        newTask.setProject(project);
        newTask.setName(name);
        newTask.setDescription(description);
        newTask.setDueDate(dueDate);
        newTask.setPriority(priority);

		return taskRepository.save(newTask);
	}

	@Override
	public void deleteTaskById(UUID taskId) {
		if(!taskRepository.existsById(taskId)) {
			throw new ResourceNotFoundException();
		}
		taskRepository.deleteById(taskId);
	}


}
