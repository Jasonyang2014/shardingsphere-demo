package com.example.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class ShardingSphereDemoApplicationTests {

    @Resource
    UserMapper userMapper;

    @Test
    void insertTest() {
        User user = new User();
        user.setName("test");
        int insert = userMapper.insert(user);
        assert insert == 1;
    }

    /**
     * Actual SQL: slave ::: SELECT  id,name  FROM t_user
     */
    @Test
    void selectTest() {
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        System.out.println(users);
    }

}
