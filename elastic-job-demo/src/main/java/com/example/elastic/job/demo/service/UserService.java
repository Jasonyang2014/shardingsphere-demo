package com.example.elastic.job.demo.service;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;

import java.util.List;

public interface UserService {

    int save(User user);

    int batchSave(List<User> users);

    List<User> findAll();

    List<User> findByGender(GenderEnum genderEnum);

    int updateUser(List<User> users);
}
