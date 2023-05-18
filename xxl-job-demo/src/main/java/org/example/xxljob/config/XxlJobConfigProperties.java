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

    @Data
    static class XxlAdminProperties {

        private String addresses;
    }

    @Data
    static class XxlJobExecutorProperties {

        private String appname;


        private String address;


        private String ip;


        private int port;


        private String logPath;


        private int logRetentionDays;
    }

}
