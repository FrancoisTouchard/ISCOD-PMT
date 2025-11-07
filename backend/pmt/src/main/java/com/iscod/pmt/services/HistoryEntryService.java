package com.iscod.pmt.services;

import java.util.List;
import java.util.UUID;

import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.models.HistoryEntry;
import com.iscod.pmt.models.Task;

public interface HistoryEntryService {

	List<HistoryEntry> findAll();

	List<HistoryEntry> findAllEntriesByProjectIdOrderedByDate(UUID projectId);

	List<HistoryEntry> findAllEntriesByTaskIdOrderedByDate(UUID taskId);
	
	void createHistoryEntry(Task task, AppUser user, String fieldName, String oldValue, String newValue);

}