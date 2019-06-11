package com.spier.service;

import java.util.List;

import com.spier.entity.User;

public interface UserService {
	List<User> queryUserByName(String name);
	String queryUser(String name);
}

