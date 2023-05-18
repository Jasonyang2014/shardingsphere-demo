package org.example.xxljob.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.context.XxlJobContext;

public class JSONUtils {

    public static String toJsonStr(XxlJobContext xxlJobContext) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(xxlJobContext);
    }
}
