package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ShardingSphereDemoApplicationTests {

	@Resource
	UserMapper userMapper;

	@Test
	void contextLoads() {
		User user = new User();
		user.setName("test insert");
		int insert = userMapper.insert(user);
		assert insert == 1;
	}

}
