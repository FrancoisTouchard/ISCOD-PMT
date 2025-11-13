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

@Table(name="project")
@Entity
public class Project {
	
	@Id
	@GeneratedValue
	private UUID id;
	
	@NotNull(message="Un projet doit avoir un nom.")
	private String name;
	
	private String description;
	
	private LocalDate startDate;
	
	@ManyToOne
	@JoinColumn(name = "id_creator")
	@JsonBackReference("creator")
	private AppUser creator;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	@JsonManagedReference("project")
	private Set<Contributor> contributors = new HashSet<>();
	
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("project-history")
    private Set<HistoryEntry> historyEntries = new HashSet<>();
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public AppUser getCreator() {
		return creator;
	}

	public void setCreator(AppUser creator) {
		this.creator = creator;
	}

	public Set<Contributor> getContributors() {
		return contributors;
	}

	public void setContributors(Set<Contributor> contributors) {
		this.contributors = contributors;
	}

}
