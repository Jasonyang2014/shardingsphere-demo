package com.example.demo;

import io.seata.config.springcloud.EnableSeataSpringConfig;
import io.seata.spring.annotation.GlobalTransactionScanner;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

//@EnableAutoDataSourceProxy
@EnableSeataSpringConfig
@SpringBootApplication
public class ShardingSphereDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShardingSphereDemoApplication.class, args);
	}

//	@Bean
	public GlobalTransactionScanner globalTransactionScanner(){
		return new GlobalTransactionScanner("default_tx_group");
	}

}
