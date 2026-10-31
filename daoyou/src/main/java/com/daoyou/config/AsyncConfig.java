package com.daoyou.config;

import com.daoyou.common.UserContext;
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class AsyncConfig {

 @Bean(name = "eventExecutor")
 public Executor eventExecutor() {
 ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 executor.setCorePoolSize(2);
 executor.setMaxPoolSize(6);
 executor.setQueueCapacity(200);
 executor.setThreadNamePrefix("daoyou-event-");
 executor.setTaskDecorator(task -> {
 String userId = UserContext.get();
 return () -> {
 try {
 UserContext.set(userId);
 task.run();
 } finally {
 UserContext.clear();
 }
 };
 });
 executor.initialize();
 return executor;
 }
}
