package com.iscod.pmt.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.pmt.models.Utilisateur;
import com.iscod.pmt.services.UtilisateurService;

@RestController
@RequestMapping("/users")
public class UtilisateurController {
	
	@Autowired
	private UtilisateurService utilisateurService;
	
	@GetMapping
	public List<Utilisateur> findAll(){
		return utilisateurService.findAll();
	}
	
	@GetMapping("/{id}")
	public Utilisateur findById(@PathVariable UUID id) {
		
		return utilisateurService.findById(id);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public UUID create(@RequestBody Utilisateur utilisateur) {
		
		return utilisateurService.create(utilisateur);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void update(@PathVariable UUID id, @RequestBody Utilisateur utilisateur) {
		
		utilisateurService.update(id, utilisateur);
	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void partialUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {

		utilisateurService.partialUpdate(id, updates);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID id) {

		utilisateurService.deleteById(id);
	}
	

}
