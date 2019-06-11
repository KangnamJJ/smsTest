
package com.spier.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.spier.entity.User;
import com.spier.service.UserService;


@RestController
public class UserControlelr {
	private Logger logger = LoggerFactory.getLogger(UserControlelr.class);
	//duobbo注解
	//@Autowired
	@Reference
	private UserService userService;

	@RequestMapping("list/{name}")
	public List<User> queryUserAll(@PathVariable String name) {
		List<User> list = this.userService.queryUserByName(name);
		System.out.println("dubbo消费者");
		return list;
	}
	@RequestMapping("list2")
	public String queryUserAll2() {
		String list = userService.queryUser("李四");
		System.out.println("dubbo消费者");
		return list;
	}
	
	@RequestMapping("/hello")
    public String HelloWorld(){
        for(int i=0;i<1000;i++){
            logger.debug("debug"+i);
            logger.info("info"+i);
            logger.warn("warn"+i);
            logger.error("error"+i);
        }
        return "Hello World!";
    }
}

