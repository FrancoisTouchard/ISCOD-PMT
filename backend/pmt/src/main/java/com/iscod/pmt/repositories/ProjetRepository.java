package com.iscod.pmt.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Projet;

public interface ProjetRepository extends CrudRepository<Projet, UUID> {

}
