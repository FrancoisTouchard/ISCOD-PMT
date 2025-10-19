package com.iscod.pmt.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.iscod.pmt.models.Contributeur;
import com.iscod.pmt.repositories.ContributeurRepository;
import com.iscod.pmt.services.ContributeurService;

public class ContributeurServiceImpl implements ContributeurService {

    @Autowired
    private ContributeurRepository contributeurRepository;

    @Override
    public List<Contributeur> findByIdIdProjet(UUID idProjet) {
        return contributeurRepository.findByIdIdProjet(idProjet);
    }

    @Override
    public List<Contributeur> findByIdIdUtilisateur(UUID idUtilisateur) {
        return contributeurRepository.findByIdIdUtilisateur(idUtilisateur);
    }
}
