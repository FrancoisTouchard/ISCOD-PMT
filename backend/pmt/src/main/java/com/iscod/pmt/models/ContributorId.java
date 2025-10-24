package com.iscod.pmt.models;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

// classe permettant de générer une clé composite représentant l'id du contributeur (= id utilisateur + id projet)

@Embeddable
public class ContributorId implements Serializable {
	private static final long serialVersionUID = 1L;

    
    @Column(name = "id_user")
    private UUID idUser;
    
    @Column(name = "id_project")
    private UUID idProject;
    
    public ContributorId() {}
    
    public ContributorId(UUID idUser, UUID idProject) {
        this.idUser = idUser;
        this.idProject = idProject;
    }
    
    public UUID getIdUser() {
        return idUser;
    }
    
    public void setIdUser(UUID idUser) {
        this.idUser = idUser;
    }
    
    public UUID getIdProject() {
        return idProject;
    }
    
    public void setIdProject(UUID idProject) {
        this.idProject = idProject;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContributorId that = (ContributorId) o;
        return Objects.equals(idUser, that.idUser) &&
               Objects.equals(idProject, that.idProject);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idUser, idProject);
    }
}