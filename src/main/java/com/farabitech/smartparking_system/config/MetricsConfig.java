package com.farabitech.smartparking_system.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class MetricsConfig {

    @Bean
    CountedAspect countedAspect(MeterRegistry registry){
        return new CountedAspect(registry);
    }

    @Bean
    TimedAspect timedAspect(MeterRegistry registry){
        return new TimedAspect(registry);
    }
}
