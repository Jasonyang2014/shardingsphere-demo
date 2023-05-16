package com.example.elastic.job.demo.jobs;

import com.example.elastic.job.demo.entity.User;
import com.example.elastic.job.demo.enums.GenderEnum;
import com.example.elastic.job.demo.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.dataflow.job.DataflowJob;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Component
public class UserDataflowJob implements DataflowJob<User> {

    private final UserService userService;

    @Override
    public List<User> fetchData(ShardingContext shardingContext) {
        String shardingParameter = shardingContext.getShardingParameter();
        GenderEnum genderEnum = GenderEnum.valueOf(shardingParameter);
        return userService.findByGender(genderEnum);
    }

    @Override
    public void processData(ShardingContext shardingContext, List<User> list) {
        log.info("list size {}", list.size());
        Date current = new Date();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<User> userList = list.stream()
                .filter(v -> offset(v.getLastTime(), current).toMinutes() < 5)
                .collect(Collectors.toList());
        int cnt = userService.updateUser(userList);
        log.info("update size {}", cnt);
    }

    private static Duration offset(Date start, Date end) {
        return Duration.between(start.toInstant(), end.toInstant());
    }
}
