package com.iscod.pmt.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.iscod.pmt.models.Contributor;
import com.iscod.pmt.models.ContributorId;
import com.iscod.pmt.models.Role;
import com.iscod.pmt.services.ContributorService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/contributors")
public class ContributorController {
	
	@Autowired
	private ContributorService contributorService;
	
		@Operation(summary = "Get all contributors of a project by project id")
	   	@GetMapping("/project/{projectId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributor> getContributeursByProjet(@PathVariable UUID projectId) {
	        return contributorService.findByIdIdProject(projectId);
	    }

		@Operation(summary = "Get all contributors of a user by user id", description = "Allows you to know all the projects where this user has a role")
	    @GetMapping("/user/{userId}")
	    @ResponseStatus(code = HttpStatus.OK)
	    public List<Contributor> getContributeursByUtilisateur(@PathVariable UUID userId) {
	        return contributorService.findByIdIdUser(userId);
	    }
	    
		@Operation(summary = "Create a new contributor by project id", description = "Add a user to the project using its email address and assign it a role")
	    @PostMapping("/project/{projectId}")
	    @ResponseStatus(code = HttpStatus.CREATED)
	    public Contributor addContributor(
	            @PathVariable UUID projectId,
	            @RequestBody Map<String, String> contributorData) {
	        
	        String email = contributorData.get("email");
	        String roleStr = contributorData.get("role");
	        Role role = Role.valueOf(roleStr);
	        
	        return contributorService.addContributorByEmail(projectId, email, role);
	    }
	   
	    
		@Operation(summary = "Partial update of a contributor by project id and user id")
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
	    
		@Operation(summary = "Delete a contributor by project id and user id")
	    @DeleteMapping("/project/{projectId}/user/{userId}")
		@ResponseStatus(code = HttpStatus.NO_CONTENT)
		public void delete(@PathVariable UUID projectId, @PathVariable UUID userId) {

			 ContributorId contributorId = new ContributorId(userId, projectId);
			 contributorService.deleteById(contributorId);
		}


	

}
