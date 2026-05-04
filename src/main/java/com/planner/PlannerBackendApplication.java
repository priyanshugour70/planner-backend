package com.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackages = "com.planner.entities")
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.planner.repositories")
public class PlannerBackendApplication {

    private static final String DEFAULT_TIMEZONE = "Asia/Kolkata";

    public static void main(String[] args) {
        setDefaultTimeZone();
        SpringApplication.run(PlannerBackendApplication.class, args);
    }

    private static void setDefaultTimeZone() {
        String tz = System.getProperty("app.timezone",
                System.getenv() != null ? System.getenv().getOrDefault("APP_TIMEZONE", DEFAULT_TIMEZONE) : DEFAULT_TIMEZONE);
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
    }
}
