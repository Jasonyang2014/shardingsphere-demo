package com.example.elastic.job.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;
import com.example.elastic.job.demo.mapper.UserMapper;
import com.example.elastic.job.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Override
    public List<User> findByGender(GenderEnum genderEnum) {
        return userMapper.selectListByGender(genderEnum);
    }

    @Override
    public int updateUser(List<User> users) {
        int i = 0;
        for (User user : users) {
            User updateUser = new User();
            updateUser.setLastTime(new Date());
            updateUser.setId(user.getId());
            int cnt = userMapper.updateById(updateUser);
            i += cnt;
        }
        return i;
    }
}
