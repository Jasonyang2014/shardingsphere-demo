package com.example.elastic.job.demo.jobs;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("execute job");
    }
}
