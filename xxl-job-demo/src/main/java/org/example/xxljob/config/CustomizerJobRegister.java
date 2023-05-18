package org.example.xxljob.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomizerJobRegister implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, IJobHandler> beans = applicationContext.getBeansOfType(IJobHandler.class);
        beans.forEach((k, v) -> {
            XxlJobExecutor.registJobHandler(k, v);
            log.info("register job handler {} success", k);
        });
    }
}
