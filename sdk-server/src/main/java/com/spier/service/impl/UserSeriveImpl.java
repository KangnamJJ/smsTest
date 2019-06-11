package com.spier.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.spier.entity.User;
import com.spier.mapper.UserMapper;
import com.spier.service.UserService;

@Service
public class UserSeriveImpl  implements UserService{

	@Autowired
	private UserMapper userMapper;


	public List<User> queryUserByName(String name) {
		List<User> list = this.userMapper.queryUserByName(name);
		System.out.println("dubbo生产者");
		return list;
	}


	@Override
	public String queryUser(String name) {
		List<User> list = this.userMapper.queryUserByName2(name);
		System.out.println("dubbo生产者");
		return list+"dubbo生产者";
	}

}
