package com.example.elastic.job.demo.service.impl;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;

public class DurationTest {

    @Test
    public void testDuration(){
        Date start = new Date();
        long endSeconds = start.getTime() + 60 * 1000L;
        Date end = new Date(endSeconds);
        long minutes = Duration.between(start.toInstant(), end.toInstant()).toMinutes();
        System.out.println(minutes);
    }
}
