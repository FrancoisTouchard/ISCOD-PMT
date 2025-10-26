package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.ContributorId;

public interface ContributorService {
	
    List<Contributor> findByIdIdProject(UUID idProject);
    
    List<Contributor> findByIdIdUser(UUID idUser);
    
    Contributor create(Contributor contributor);

	Contributor partialUpdate(ContributorId id, Map<String, Object> updates);

}
