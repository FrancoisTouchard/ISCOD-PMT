package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.Projet;

public interface ProjetService {

	List<Projet> findAll();

	Projet findById(UUID id);

	Projet create(Projet projet, UUID createurId);
	
	void update(UUID id, Projet projet);

	void partialUpdate(UUID id, Map<String, Object> updates);

	void deleteById(UUID id);

}
