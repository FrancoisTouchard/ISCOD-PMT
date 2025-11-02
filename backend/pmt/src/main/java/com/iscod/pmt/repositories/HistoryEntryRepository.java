package com.iscod.pmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.HistoryEntry;

public interface HistoryEntryRepository extends CrudRepository<HistoryEntry, UUID> {
	
	List<HistoryEntry> findByProjectIdOrderByModifiedAtDesc(UUID projectId);
	
	List<HistoryEntry> findByTaskIdOrderByModifiedAtDesc(UUID taskId);

}
