package com.iscod.pmt.services;

import java.util.List;
import java.util.UUID;

import com.iscod.pmt.models.Contributor;

public interface ContributorService {
	
    List<Contributor> findByIdIdProject(UUID idProject);
    
    List<Contributor> findByIdIdUser(UUID idUser);
    
    Contributor create(Contributor contributor);

}
