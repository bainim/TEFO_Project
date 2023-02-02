package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.dto.UserDto;
import com.talan.empreintecarbone.model.User;

import java.util.List;

public interface UserService {
	User save(UserDto user);

	User save(User user);

	List<User> findAll();

	User findOne(String id);

	User findByUsername(String username);

	void verifyEmail(User user);

	void updateUserConnexionDateAndVersion(User user,String version);

}
