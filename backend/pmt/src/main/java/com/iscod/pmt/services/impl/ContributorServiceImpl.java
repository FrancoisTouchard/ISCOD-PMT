package com.iscod.pmt.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.repositories.ContributorRepository;
import com.iscod.pmt.services.ContributorService;

@Service
public class ContributorServiceImpl implements ContributorService {

    @Autowired
    private ContributorRepository contributorRepository;

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
}
