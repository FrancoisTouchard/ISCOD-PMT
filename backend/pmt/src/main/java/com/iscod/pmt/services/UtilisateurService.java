package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.Utilisateur;

public interface UtilisateurService {

	List<Utilisateur> findAll();
	
	Utilisateur findById(UUID id);

	UUID create(Utilisateur utilisateur);

	void update(UUID id, Utilisateur utilisateur);

	void partialUpdate(UUID id, Map<String, Object> updates);

	void deleteById(UUID id);

}