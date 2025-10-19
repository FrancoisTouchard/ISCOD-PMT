package com.iscod.pmt.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// classe permettant de générer une clé composite représentant l'id du contributeur (= id utilisateur + id projet)

@Embeddable
public class ContributeurId implements Serializable {
	private static final long serialVersionUID = 1L;

    
    @Column(name = "id_utilisateur")
    private UUID idUtilisateur;
    
    @Column(name = "id_projet")
    private UUID idProjet;
    
    public ContributeurId() {}
    
    public ContributeurId(UUID idUtilisateur, UUID idProjet) {
        this.idUtilisateur = idUtilisateur;
        this.idProjet = idProjet;
    }
    
    public UUID getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(UUID idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public UUID getIdProjet() {
        return idProjet;
    }
    
    public void setIdProjet(UUID idProjet) {
        this.idProjet = idProjet;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributeurId that = (ContributeurId) o;
        return Objects.equals(idUtilisateur, that.idUtilisateur) &&
               Objects.equals(idProjet, that.idProjet);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idUtilisateur, idProjet);
    }
}