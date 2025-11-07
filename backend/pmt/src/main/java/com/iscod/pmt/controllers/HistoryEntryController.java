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

import com.iscod.pmt.models.HistoryEntry;
import com.iscod.pmt.services.HistoryEntryService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/history")
public class HistoryEntryController {
	
	@Autowired
	private HistoryEntryService historyEntryService;
	
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<HistoryEntry> findAll(){
		return historyEntryService.findAll();
	}
	
	@GetMapping("/project/{projectId}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<HistoryEntry> findAllEntriesByProjectIdOrderedByDate(@PathVariable UUID projectId){
		return historyEntryService.findAllEntriesByProjectIdOrderedByDate(projectId);
	}
	
	@GetMapping("/task/{taskId}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<HistoryEntry> findAllEntriesByTaskIdOrderedByDate(@PathVariable UUID taskId){
		return historyEntryService.findAllEntriesByTaskIdOrderedByDate(taskId);
	}
	
}
