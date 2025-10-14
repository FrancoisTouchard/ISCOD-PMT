package com.iscod.pmt.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.Projet;
import com.iscod.pmt.repositories.ProjetRepository;
import com.iscod.pmt.services.ProjetService;

@Service
public class ProjetServiceImpl implements ProjetService {
	
	@Autowired
	private ProjetRepository projetRepository;


	@Override
	public List<Projet> findAll() {
		List<Projet> liste = new ArrayList<Projet>();
		
		projetRepository.findAll().forEach(liste::add);
		
		return liste;
	}
	
	@Override
	public Projet findById(UUID id) {
	if(projetRepository.findById(id).isPresent()) {
			
			return projetRepository.findById(id).get();
		}
		
		return projetRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
	}


	@Override
	public UUID create(Projet projet) {
		return projetRepository.save(projet).getId();
	}

	@Override
	public void update(UUID id, Projet projet) {
		projet.setId(id);
		projetRepository.save(projet);
	}

	@Override
	public void partialUpdate(UUID id, Map<String, Object> updates) {
		Projet projetToUpdate = projetRepository.findById(id).get();
		
		for(String key : updates.keySet()) {
			
			switch(key) {
			case "nom" : {
				projetToUpdate.setNom((String)updates.get(key));
				break;
			}
			case "description" : {
				projetToUpdate.setNom((String)updates.get(key));
				break;
			}
			case "dateDebut" : {
				projetToUpdate.setDateDebut((LocalDate)updates.get(key));
				break;
			}
			}
		}
		
		projetRepository.save(projetToUpdate);
	}

	@Override
	public void deleteById(UUID id) {
		projetRepository.deleteById(id);
	}


}
