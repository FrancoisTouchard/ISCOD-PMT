package com.iscod.pmt.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.exceptions.ResourceNotFoundException;
import com.iscod.pmt.models.AppUser;
import com.iscod.pmt.repositories.UserRepository;
import com.iscod.pmt.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<AppUser> findAll() {
		List<AppUser> list = new ArrayList<AppUser>();
		userRepository.findAll().forEach(list::add);
		
		return list;
	}
	
	@Override
	public AppUser findById(UUID id) {
	if(userRepository.findById(id).isPresent()) {
			
			return userRepository.findById(id).get();
		}
		
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable ou inexistant"));
	}
	
	@Override
	public AppUser findByEmail(String email) {
	if(userRepository.findByEmail(email).isPresent()) {
			
			return userRepository.findByEmail(email).get();
		}
		
		return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur avec cet email"));
	}

	@Override
	public UUID create(AppUser user) {
		return userRepository.save(user).getId();
	}

	@Override
	public void update(UUID id, AppUser user) {
		user.setId(id);
		userRepository.save(user);
	}

	@Override
	public void partialUpdate(UUID id, Map<String, Object> updates) {
		AppUser userToUpdate = userRepository.findById(id).get();
		
		for(String key : updates.keySet()) {
			
			switch(key) {
			case "name" : {
				userToUpdate.setName((String)updates.get(key));
				break;
			}
			case "email" : {
				userToUpdate.setEmail((String)updates.get(key));
				break;
			}
			case "password" : {
				userToUpdate.setPassword((String)updates.get(key));
				break;
			}
			}
		}
		
		userRepository.save(userToUpdate);
	}

	@Override
	public void deleteById(UUID id) {
		userRepository.deleteById(id);
	}

}
