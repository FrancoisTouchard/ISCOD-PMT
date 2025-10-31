package com.iscod.pmt.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Task;

public interface TaskRepository extends CrudRepository<Task, UUID> {
	
	List<Task> findByProjectId(UUID projectId);

}
