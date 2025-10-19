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

@Table(name="utilisateur")
@Entity
public class Utilisateur {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@NotNull(message="Le nom est obligatoire")
	private String nom;
	
	private String email;
	
	private String password;
	
	
	@OneToMany(mappedBy = "createur", cascade = CascadeType.ALL)
	@JsonManagedReference("createur")
	private Set<Projet> projets = new HashSet<Projet>();

	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
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
	
	public Set<Projet> getProjets() {
		return projets;
	}

	public void setProjets(Set<Projet> projets) {
		this.projets = projets;
	}
	
	

}
