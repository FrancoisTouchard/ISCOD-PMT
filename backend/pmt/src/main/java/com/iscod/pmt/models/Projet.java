package com.iscod.pmt.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name="projet")
@Entity
public class Projet {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@NotNull(message="Un projet doit avoir un nom.")
	private String nom;
	
	private String description;
	
	private LocalDate dateDebut;
	
	@ManyToOne
	@JoinColumn(name = "id_createur")
	@JsonBackReference("createur")
	private Utilisateur createur;
	
	@OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
	@JsonManagedReference("projet")
	private Set<Contributeur> contributeurs = new HashSet<>();
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(LocalDate dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Utilisateur getCreateur() {
		return createur;
	}

	public void setCreateur(Utilisateur createur) {
		this.createur = createur;
	}

	public Set<Contributeur> getContributeurs() {
		return contributeurs;
	}

	public void setContributeurs(Set<Contributeur> contributeurs) {
		this.contributeurs = contributeurs;
	}

}
