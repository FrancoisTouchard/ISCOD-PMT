package com.iscod.pmt.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Entity that stores user's login data such as name, email, password. Also contains the projects that were created by the user.")
@Table(name="app_user")
@Entity
public class AppUser {
	
	@Id
	@GeneratedValue
	private UUID id; 
	
	@NotNull(message="Le nom est obligatoire")
	private String name;
	
	private String email;
	
	private String password;
	
	@Schema(description = "Only the projects created by the user")
	@OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
	@JsonManagedReference("creator")
	private Set<Project> projects = new HashSet<Project>();

	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	

}
