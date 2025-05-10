package com.siemens.internship;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.jupiter.api.Assertions.*;

public class AsyncConfigurationTest {
    @Test
    public void test() {
        AsyncConfiguration asyncConfiguration = new AsyncConfiguration();
        Executor executor = asyncConfiguration.asyncExecutor();

        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);

        ThreadPoolTaskExecutor tpExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(5, tpExecutor.getCorePoolSize());
        assertEquals(5, tpExecutor.getMaxPoolSize());
        assertEquals(150, tpExecutor.getThreadPoolExecutor().getQueue().remainingCapacity());
    }
}
