package com.iscod.pmt.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.pmt.models.Project;
import com.iscod.pmt.services.ProjectService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/projects")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@GetMapping
	public List<Project> findAll(){
		return projectService.findAll();
	}
	
	@GetMapping("/{id}")
	public Project findById(@PathVariable UUID id) {
		
		return projectService.findById(id);
	}
	
	@GetMapping("/my-projects/{userId}")
	public List<Project> getProjetsByUtilisateurId(@PathVariable UUID userId) {
	    return projectService.getProjetsByUtilisateurId(userId);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Project create(@RequestBody Project project, @RequestParam UUID creatorId) {
	    return projectService.create(project, creatorId);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void update(@PathVariable UUID id, @RequestBody Project project) {
		
		projectService.update(id, project);
	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void partialUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {

		projectService.partialUpdate(id, updates);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID id) {

		projectService.deleteById(id);
	}
	

}
