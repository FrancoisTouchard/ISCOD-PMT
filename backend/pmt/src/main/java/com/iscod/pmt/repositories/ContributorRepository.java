package com.iscod.pmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.ContributorId;

public interface ContributorRepository extends CrudRepository<Contributor, ContributorId> {
	// Implémentation générée automatiquement via le nom de méthode
    List<Contributor> findByIdIdProject(UUID idProjet);
    List<Contributor> findByIdIdUser(UUID idUser);

}