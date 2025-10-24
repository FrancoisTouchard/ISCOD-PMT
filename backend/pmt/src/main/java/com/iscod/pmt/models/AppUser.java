package com.iscod.pmt.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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

	public void setName(String nom) {
		this.name = nom;
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
	
	public Set<Project> getProjets() {
		return projects;
	}

	public void setProjets(Set<Project> projects) {
		this.projects = projects;
	}
	
	

}
