package com.iscod.pmt.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.Project;

public interface ProjectRepository extends CrudRepository<Project, UUID> {

}
