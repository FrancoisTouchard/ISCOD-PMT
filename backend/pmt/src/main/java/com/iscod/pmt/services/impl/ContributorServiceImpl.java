package com.iscod.pmt.services.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceAlreadyExistsException;
import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.ContributorId;
import com.iscod.pmt.models.Project;
import com.iscod.pmt.models.Role;
import com.iscod.pmt.repositories.ContributorRepository;
import com.iscod.pmt.repositories.ProjectRepository;
import com.iscod.pmt.repositories.UserRepository;
import com.iscod.pmt.services.ContributorService;

@Service
public class ContributorServiceImpl implements ContributorService {

    @Autowired
    private ContributorRepository contributorRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Contributor> findByIdIdProject(UUID idProject) {
        return contributorRepository.findByIdIdProject(idProject);
    }

    @Override
    public List<Contributor> findByIdIdUser(UUID idUser) {
        return contributorRepository.findByIdIdUser(idUser);
    }

	@Override
	public Contributor create(Contributor contributeur) {
		return contributorRepository.save(contributeur);
	}
	
    @Override
    public Contributor addContributorByEmail(UUID projectId, String email, Role role) {
        Project project = projectRepository.findById(projectId)
        		.orElseThrow(() -> new ResourceNotFoundException("Projet introuvable ou inexistant")) ;

        
        		AppUser user = userRepository.findByEmail(email)
                		.orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable ou inexistant")) ;

        
        ContributorId contributorId = new ContributorId(user.getId(), projectId);
        if (contributorRepository.existsById(contributorId)) {
            throw new ResourceAlreadyExistsException("Ce contributeur fait déjà partie du projet");
        }
        
        Contributor newContributor = new Contributor();
        newContributor.setId(contributorId);
        newContributor.setRole(role);
        newContributor.setUser(user);
        newContributor.setProject(project);
        
        return contributorRepository.save(newContributor);
    }

	@Override
	public Contributor partialUpdate(ContributorId id, Map<String, Object> updates) {
		
		Contributor contributorToUpdate = contributorRepository.findById(id).get();
		
		for(String key : updates.keySet()) {
			
			switch(key) {
			case "role" : {
				contributorToUpdate.setRole((Role)updates.get(key));
				break;
			}
			}
		}
		
		return contributorRepository.save(contributorToUpdate);
	}

	@Override
	public void deleteById(ContributorId id) {
		if(!contributorRepository.existsById(id)) {
			throw new ResourceNotFoundException("Utilisateur introuvable ou inexistant");
		}
		contributorRepository.deleteById(id);
	}


}
