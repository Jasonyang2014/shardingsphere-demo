package com.example.elastic.job.demo.service.impl;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.mapper.UserMapper;
import com.example.elastic.job.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    @Override
    public int save(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int batchSave(List<User> users) {
        return userMapper.batchSave(users);
    }

    @Override
    public List<User> findAll() {
        return userMapper.selectList(null);
    }
}
