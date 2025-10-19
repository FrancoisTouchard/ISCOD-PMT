package com.iscod.pmt.services.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.Contributeur;
import com.iscod.pmt.models.Projet;
import com.iscod.pmt.models.Role;
import com.iscod.pmt.models.Utilisateur;
import com.iscod.pmt.repositories.ContributeurRepository;
import com.iscod.pmt.repositories.ProjetRepository;
import com.iscod.pmt.services.ProjetService;
import com.iscod.pmt.services.UtilisateurService;


@Service
public class ProjetServiceImpl implements ProjetService {
	
	@Autowired
	private ProjetRepository projetRepository;
	
	@Autowired
	private ContributeurRepository contributeurRepository;
	
	@Autowired
	private UtilisateurService utilisateurService;


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
	@Transactional
	public Projet create(Projet projet, UUID createurId) {
	    Utilisateur createur = utilisateurService.findById(createurId);
	    
	    projet.setCreateur(createur);
	    Projet projetToSave = projetRepository.save(projet);
	    
	    // création du contributeur (admin) en même temps que la création du projet
	    Contributeur contributeurAdmin = new Contributeur(createur, projetToSave, Role.ADMINISTRATEUR);
	    contributeurRepository.save(contributeurAdmin);
	    
	    return projetToSave;
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
				projetToUpdate.setDescription((String)updates.get(key));
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
