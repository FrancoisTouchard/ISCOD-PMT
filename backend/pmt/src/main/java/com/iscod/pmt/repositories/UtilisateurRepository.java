package com.iscod.pmt.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, UUID> {

}
