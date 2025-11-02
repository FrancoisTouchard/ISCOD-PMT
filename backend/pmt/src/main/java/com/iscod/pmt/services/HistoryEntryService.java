package com.iscod.pmt.services;

import java.util.List;
import java.util.UUID;

import com.iscod.pmt.models.HistoryEntry;

public interface HistoryEntryService {

	List<HistoryEntry> findAll();

	List<HistoryEntry> findAllEntriesByProjectIdOrderedByDate(UUID projectId);

	List<HistoryEntry> findAllEntriesByTaskIdOrderedByDate(UUID taskId);

}