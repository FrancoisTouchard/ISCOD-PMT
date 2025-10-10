package com.iscod.pmt.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.iscod.pmt.models.User;

public interface UserRepository extends CrudRepository<User, UUID> {

}
