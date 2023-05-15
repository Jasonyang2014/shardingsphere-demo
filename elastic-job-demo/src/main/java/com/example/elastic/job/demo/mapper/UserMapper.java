package com.example.elastic.job.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    int batchSave(List<User> users);

    List<User> selectListByGender(GenderEnum genderEnum);
}
