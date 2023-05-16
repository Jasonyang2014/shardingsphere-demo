package com.example.elastic.job.demo.jobs;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;
import com.example.elastic.job.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MyJob implements SimpleJob {

    private final UserService userService;

    public MyJob(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("execute job" + shardingContext);
        String shardingParameter = shardingContext.getShardingParameter();
        GenderEnum genderEnum = GenderEnum.valueOf(shardingParameter);
        List<User> females = userService.findByGender(genderEnum);
        females.forEach(System.out::println);
    }
}
