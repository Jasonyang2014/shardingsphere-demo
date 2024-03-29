package com.example.elastic.job.demo.jobs;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;
import com.example.elastic.job.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;
import org.springframework.util.JdkIdGenerator;
import org.springframework.util.SimpleIdGenerator;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class MyJob implements SimpleJob {

    private final UserService userService;
    private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();

    public MyJob(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("execute job" + shardingContext);
        Random random = new Random();
        if (random.nextInt(3) % 2 == 0) {
            log.error("test error");
            throw new RuntimeException("test email error");
        }
        String shardingParameter = shardingContext.getShardingParameter();
        GenderEnum genderEnum = GenderEnum.valueOf(shardingParameter);
        User user = new User();
        user.setName("z" + idGenerator.generateId().toString().replaceAll("-", "").substring(30));
        user.setGender(genderEnum);
        user.setLastTime(new Date());
        userService.save(user);
        log.info("save user {}", user);
    }
}
