package com.siemens.internship;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    //Added my own executor for proper @Async execution
    @Bean("myExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(150);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("AsyncTaskThread-");
        executor.initialize();
        return executor;
    }

}
