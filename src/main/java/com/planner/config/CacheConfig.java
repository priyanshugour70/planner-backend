package com.planner.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                new ConcurrentMapCache("sessions"),
                new ConcurrentMapCache("users"),
                new ConcurrentMapCache("goals"),
                new ConcurrentMapCache("tasks"),
                new ConcurrentMapCache("notes"),
                new ConcurrentMapCache("habits"),
                new ConcurrentMapCache("journals"),
                new ConcurrentMapCache("reminders"),
                new ConcurrentMapCache("transactions"),
                new ConcurrentMapCache("analytics")
        ));
        return cacheManager;
    }
}
