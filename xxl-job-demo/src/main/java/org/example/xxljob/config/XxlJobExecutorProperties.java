package org.example.xxljob.config;

import lombok.Data;

@Data
public class XxlJobExecutorProperties {

    private String appname;


    private String address;


    private String ip;


    private int port;


    private String logPath;


    private int logRetentionDays;
}
