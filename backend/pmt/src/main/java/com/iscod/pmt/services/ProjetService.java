package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.Projet;

public interface ProjetService {

	List<Projet> findAll();

	UUID create(Projet projet);

	Projet findById(UUID id);

	void update(UUID id, Projet projet);

	void partialUpdate(UUID id, Map<String, Object> updates);

	void deleteById(UUID id);

}
