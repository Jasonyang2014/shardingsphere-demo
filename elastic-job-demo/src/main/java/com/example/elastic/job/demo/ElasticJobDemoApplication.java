package com.example.elastic.job.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.example.elastic.job.demo.mapper"})
public class ElasticJobDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticJobDemoApplication.class);
    }
}
