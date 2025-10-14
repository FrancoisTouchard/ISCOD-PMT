package com.iscod.pmt.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.Utilisateur;
import com.iscod.pmt.repositories.UtilisateurRepository;
import com.iscod.pmt.services.UtilisateurService;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {
	
	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@Override
	public List<Utilisateur> findAll() {
		List<Utilisateur> liste = new ArrayList<Utilisateur>();
		utilisateurRepository.findAll().forEach(liste::add);
		
		return liste;
	}
	
	@Override
	public Utilisateur findById(UUID id) {
	if(utilisateurRepository.findById(id).isPresent()) {
			
			return utilisateurRepository.findById(id).get();
		}
		
		return utilisateurRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}

	@Override
	public UUID create(Utilisateur utilisateur) {
		return utilisateurRepository.save(utilisateur).getId();
	}

	@Override
	public void update(UUID id, Utilisateur utilisateur) {
		utilisateur.setId(id);
		utilisateurRepository.save(utilisateur);
	}

	@Override
	public void partialUpdate(UUID id, Map<String, Object> updates) {
		Utilisateur utilisateurToUpdate = utilisateurRepository.findById(id).get();
		
		for(String key : updates.keySet()) {
			
			switch(key) {
			case "nom" : {
				utilisateurToUpdate.setNom((String)updates.get(key));
				break;
			}
			case "email" : {
				utilisateurToUpdate.setEmail((String)updates.get(key));
				break;
			}
			case "password" : {
				utilisateurToUpdate.setPassword((String)updates.get(key));
				break;
			}
			}
		}
		
		utilisateurRepository.save(utilisateurToUpdate);
	}

	@Override
	public void deleteById(UUID id) {
		utilisateurRepository.deleteById(id);
	}



}
