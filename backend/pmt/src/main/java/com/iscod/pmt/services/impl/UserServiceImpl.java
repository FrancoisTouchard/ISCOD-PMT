package com.iscod.pmt.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iscod.pmt.models.User;
import com.iscod.pmt.repositories.UserRepository;
import com.iscod.pmt.services.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> findAll() {
		List<User> liste = new ArrayList<User>();
		
		userRepository.findAll().forEach(liste::add);
		
		return liste;
	}

}
