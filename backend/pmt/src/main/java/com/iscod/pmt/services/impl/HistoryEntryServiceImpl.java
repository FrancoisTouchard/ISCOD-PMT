package com.iscod.pmt.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.models.HistoryEntry;
import com.iscod.pmt.repositories.HistoryEntryRepository;
import com.iscod.pmt.services.HistoryEntryService;

@Service
public class HistoryEntryServiceImpl implements HistoryEntryService {
	
	@Autowired
	private HistoryEntryRepository historyEntryRepository;

	@Override
	public List<HistoryEntry> findAll() {
		List<HistoryEntry> liste = new ArrayList<HistoryEntry>();
		historyEntryRepository.findAll().forEach(liste::add);
		
		return liste;
	}

	@Override
	public List<HistoryEntry> findAllEntriesByProjectIdOrderedByDate(UUID projectId) {
		List<HistoryEntry> liste = new ArrayList<HistoryEntry>();
		historyEntryRepository.findByProjectIdOrderByModifiedAtDesc(projectId).forEach(liste::add);
		
		return liste;
	}

	@Override
	public List<HistoryEntry> findAllEntriesByTaskIdOrderedByDate(UUID taskId) {
		List<HistoryEntry> liste = new ArrayList<HistoryEntry>();
		historyEntryRepository.findByTaskIdOrderByModifiedAtDesc(taskId).forEach(liste::add);
		
		return liste;
	}

}
