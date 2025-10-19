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

@Table(name = "contributeur")
@Entity
public class Contributeur {
    
    @EmbeddedId
    private ContributeurId id;
    
    @ManyToOne
    @MapsId("idUtilisateur")
    @JoinColumn(name = "id_utilisateur")
    @JsonBackReference("utilisateur")
    private Utilisateur utilisateur;
    
    @ManyToOne
    @MapsId("idProjet")
    @JoinColumn(name = "id_projet")
    @JsonBackReference("projet")
    private Projet projet;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    public Contributeur() {}
    
    public Contributeur(Utilisateur utilisateur, Projet projet, Role role) {
        this.id = new ContributeurId(utilisateur.getId(), projet.getId());
        this.utilisateur = utilisateur;
        this.projet = projet;
        this.role = role;
    }
    
    public ContributeurId getId() {
        return id;
    }
    
    public void setId(ContributeurId id) {
        this.id = id;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public Projet getProjet() {
        return projet;
    }
    
    public void setProjet(Projet projet) {
        this.projet = projet;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}