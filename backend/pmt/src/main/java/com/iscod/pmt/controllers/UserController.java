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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.services.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping
	public List<AppUser> findAll(){
		return userService.findAll();
	}
	
	@GetMapping("/{id}")
	public AppUser findById(@PathVariable UUID id) {
		
		return userService.findById(id);
	}
	
	@GetMapping("/{email}")
	public AppUser findByEmail(@PathVariable String email) {
		
		return userService.findByEmail(email);
	}
	
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public UUID create(@RequestBody AppUser user) {
		
		return userService.create(user);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void update(@PathVariable UUID id, @RequestBody AppUser user) {
		
		userService.update(id, user);
	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void partialUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {

		userService.partialUpdate(id, updates);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID id) {

		userService.deleteById(id);
	}
	

}
