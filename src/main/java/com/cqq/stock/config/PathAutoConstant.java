package com.cqq.stock.config;

import com.cqq.stock.constants.PathConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathAutoConstant {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public PathConstant getPathConstant() {
        return new PathConstant();

    }
}
