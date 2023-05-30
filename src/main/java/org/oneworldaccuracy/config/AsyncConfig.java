package org.oneworldaccuracy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Set the core pool size to 2
        executor.setMaxPoolSize(2); // Set the maximum pool size to 2
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("workItemTaskExecutor-");
        executor.initialize();
        return executor;
    }
}
