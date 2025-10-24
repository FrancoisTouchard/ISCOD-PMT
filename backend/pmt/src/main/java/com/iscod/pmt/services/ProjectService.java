package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.Project;

public interface ProjectService {

	List<Project> findAll();

	Project findById(UUID id);

	Project create(Project project, UUID creatorId);
	
	void update(UUID id, Project project);

	void partialUpdate(UUID id, Map<String, Object> updates);

	void deleteById(UUID id);

	List<Project> getProjetsByUtilisateurId(UUID userId);

}
