package org.example.xxljob.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobConfigProperties {

    private XxlAdminProperties admin;


    private String accessToken;


    private XxlJobExecutorProperties executor;
}
