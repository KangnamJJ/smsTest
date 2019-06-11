package com.spier.mapper;

import java.util.List;

import javax.websocket.server.PathParam;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.spier.entity.User;

@Mapper
public interface UserMapper {

	@Select("select * from user where name like '%${value}%'")
	public List<User> queryUserByName(String name);
   
	
	public List<User> queryUserByName2(@PathParam("name")String name);
}
