package com.iscod.pmt.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.iscod.pmt.models.AppUser;

public interface UserService {

	List<AppUser> findAll();
	
	AppUser findById(UUID id);

	UUID create(AppUser user);

	void update(UUID id, AppUser user);

	void partialUpdate(UUID id, Map<String, Object> updates);

	void deleteById(UUID id);

}