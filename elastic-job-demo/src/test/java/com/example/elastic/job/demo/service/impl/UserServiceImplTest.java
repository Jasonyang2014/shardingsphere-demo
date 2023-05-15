package com.example.elastic.job.demo.service.impl;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;
import com.example.elastic.job.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Resource
    UserService userService;

    @Test
    void save() {
        User user = new User();
        user.setName("zhangsan");
        user.setGender(GenderEnum.MALE);
        userService.save(user);
    }

    @Test
    void batchSave() {
        Random random = new Random();
        ArrayList<User> users = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            int nexted = random.nextInt(3);
            User user = new User();
            user.setName("zhangSan" + i);
            user.setGender(GenderEnum.valueOf(nexted));
            users.add(user);
        }
        userService.batchSave(users);
    }

    @Test
    void findAll() {
        List<User> all = userService.findAll();
        all.forEach(System.out::println);
    }
}