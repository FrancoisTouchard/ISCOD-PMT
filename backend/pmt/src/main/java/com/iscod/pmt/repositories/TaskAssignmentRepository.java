package com.iscod.pmt.repositories;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.TaskAssignment;
import com.iscod.pmt.models.TaskAssignmentId;

public interface TaskAssignmentRepository extends CrudRepository<TaskAssignment, TaskAssignmentId> {

}
