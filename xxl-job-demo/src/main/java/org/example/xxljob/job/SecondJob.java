package org.example.xxljob.job;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.xxljob.utils.JSONUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecondJob extends IJobHandler {

    @Override
    public void execute() throws Exception {
        XxlJobContext xxlJobContext = XxlJobContext.getXxlJobContext();
        log.info("second job {}", JSONUtils.toJsonStr(xxlJobContext));
    }
}
