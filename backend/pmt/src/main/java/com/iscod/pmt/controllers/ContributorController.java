package com.iscod.pmt.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.ContributorId;
import com.iscod.pmt.models.Role;
import com.iscod.pmt.services.ContributorService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/contributors")
public class ContributorController {
	
	@Autowired
	private ContributorService contributorService;
	
	   @GetMapping("/project/{projectId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributor> getContributeursByProjet(@PathVariable UUID projectId) {
	        return contributorService.findByIdIdProject(projectId);
	    }

	    @GetMapping("/user/{userId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributor> getContributeursByUtilisateur(@PathVariable UUID userId) {
	        return contributorService.findByIdIdUser(userId);
	    }
	    
	    @PatchMapping("/project/{projectId}/user/{userId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public Contributor partialUpdate(
	            @PathVariable UUID projectId,
	            @PathVariable UUID userId,
	            @RequestBody Map<String, Object> updates) {

	        ContributorId contributorId = new ContributorId(userId, projectId);

	        if (updates.containsKey("role")) {
	            String roleStr = (String) updates.get("role");
	            updates.put("role", Role.valueOf(roleStr));
	        }

	        return contributorService.partialUpdate(contributorId, updates);
	    }


	

}
