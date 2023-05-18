package org.example.xxljob.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.xxljob.utils.JSONUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirstJob {

    @SneakyThrows
    @XxlJob(value = "first", init = "init", destroy = "destroy")
    public void first() {
        XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
        String xxlContext = JSONUtils.toJsonStr(xxlJobContext);
        log.info("first hello {}", xxlContext);
    }




    private void init() {
        log.info("init first job");
    }

    private void destroy() {
        log.info("destroy job first");
    }
}
