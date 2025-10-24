package com.iscod.pmt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Table(name = "contributor")
@Entity
public class Contributor {
    
    @EmbeddedId
    private ContributorId id;
    
    @ManyToOne
    @MapsId("idUser")
    @JoinColumn(name = "id_user")
    @JsonBackReference("user")
    private AppUser user;
    
    @ManyToOne
    @MapsId("idProject")
    @JoinColumn(name = "id_project")
    @JsonBackReference("project")
    private Project project;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    public Contributor() {}
    
    public Contributor(AppUser user, Project project, Role role) {
        this.id = new ContributorId(user.getId(), project.getId());
        this.user = user;
        this.project = project;
        this.role = role;
    }
    
    public ContributorId getId() {
        return id;
    }
    
    public void setId(ContributorId id) {
        this.id = id;
    }
    
    public AppUser getUser() {
        return user;
    }
    
    public void setUser(AppUser user) {
        this.user = user;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}