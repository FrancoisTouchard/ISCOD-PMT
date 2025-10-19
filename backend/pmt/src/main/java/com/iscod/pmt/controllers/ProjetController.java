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

import com.iscod.pmt.models.Projet;
import com.iscod.pmt.services.ProjetService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/projets")
public class ProjetController {
	
	@Autowired
	private ProjetService projetService;
	
	@GetMapping
	public List<Projet> findAll(){
		return projetService.findAll();
	}
	
	@GetMapping("/{id}")
	public Projet findById(@PathVariable UUID id) {
		
		return projetService.findById(id);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Projet create(@RequestBody Projet projet, @RequestParam UUID createurId) {
	    return projetService.create(projet, createurId);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void update(@PathVariable UUID id, @RequestBody Projet projet) {
		
		projetService.update(id, projet);
	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void partialUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {

		projetService.partialUpdate(id, updates);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID id) {

		projetService.deleteById(id);
	}
	

}
