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
import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.Project;
import com.iscod.pmt.models.Role;
import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.repositories.ProjectRepository;
import com.iscod.pmt.services.ContributorService;
import com.iscod.pmt.services.ProjectService;
import com.iscod.pmt.services.UserService;


@Service
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private ContributorService contributeurService;
	
	@Autowired
	private UserService utilisateurService;


	@Override
	public List<Project> findAll() {
		List<Project> liste = new ArrayList<Project>();
		
		projectRepository.findAll().forEach(liste::add);
		
		return liste;
	}
	
	@Override
	public Project findById(UUID id) {
	if(projectRepository.findById(id).isPresent()) {
			
			return projectRepository.findById(id).get();
		}
		
		return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant"));
	}


	@Override
	@Transactional
	public Project create(Project project, UUID createurId) {
	    AppUser createur = utilisateurService.findById(createurId);
	    
	    project.setCreator(createur);
	    Project projetToSave = projectRepository.save(project);
	    
	    // création du contributeur (admin) en même temps que la création du projet
	    Contributor contributeurAdmin = new Contributor(createur, projetToSave, Role.ADMINISTRATEUR);
	    contributeurService.create(contributeurAdmin);
	    
	    return projetToSave;
	}

	@Override
	public void update(UUID id, Project project) {
		project.setId(id);
		projectRepository.save(project);
	}

	@Override
	public void partialUpdate(UUID id, Map<String, Object> updates) {
		Project projectToUpdate = projectRepository.findById(id).get();
		
		for(String key : updates.keySet()) {
			
			switch(key) {
			case "name" : {
				projectToUpdate.setName((String)updates.get(key));
				break;
			}
			case "description" : {
				projectToUpdate.setDescription((String)updates.get(key));
				break;
			}
			case "startDate" : {
				projectToUpdate.setStartDate((LocalDate)updates.get(key));
				break;
			}
			}
		}
		
		projectRepository.save(projectToUpdate);
	}

	@Override
	public void deleteById(UUID id) {
		projectRepository.deleteById(id);
	}

	@Override
	public List<Project> getProjetsByUtilisateurId(UUID userId) {
		// Récupérer toutes les instances de Contributeur rattachées à un utilisateur
	    List<Contributor> contributeurs = contributeurService.findByIdIdUser(userId);
	    
	    List<Project> projects = new ArrayList<>();
	    for (Contributor contributeur : contributeurs) {
	    	projects.add(contributeur.getProject());
	    }
	    return projects;
	}


}
