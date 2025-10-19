package com.iscod.pmt.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.pmt.models.Contributeur;
import com.iscod.pmt.services.ContributeurService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/contributeurs")
public class ContributeurController {
	
	@Autowired
	private ContributeurService contributeurService;
	
	   @GetMapping("/projet/{projetId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributeur> getContributeursByProjet(@PathVariable UUID projetId) {
	        return contributeurService.findByIdIdProjet(projetId);
	    }

	    @GetMapping("/utilisateur/{utilisateurId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributeur> getContributeursByUtilisateur(@PathVariable UUID utilisateurId) {
	        return contributeurService.findByIdIdUtilisateur(utilisateurId);
	    }
	

}
