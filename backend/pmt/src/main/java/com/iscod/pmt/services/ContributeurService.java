package com.iscod.pmt.services;

import java.util.List;
import java.util.UUID;

import com.iscod.pmt.models.Contributeur;

public interface ContributeurService {
	
    List<Contributeur> findByIdIdProjet(UUID idProjet);
    
    List<Contributeur> findByIdIdUtilisateur(UUID idUtilisateur);

}
