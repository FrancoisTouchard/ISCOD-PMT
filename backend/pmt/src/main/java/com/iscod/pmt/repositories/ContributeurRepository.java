package com.iscod.pmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Contributeur;
import com.iscod.pmt.models.ContributeurId;

public interface ContributeurRepository extends CrudRepository<Contributeur, ContributeurId> {
    List<Contributeur> findByIdIdProjet(UUID idProjet);
    List<Contributeur> findByIdIdUtilisateur(UUID idUtilisateur);

}