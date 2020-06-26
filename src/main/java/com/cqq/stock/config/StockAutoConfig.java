package com.cqq.stock.config;

import com.cqq.stock.constants.StockConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StockAutoConfig {

    @Bean
    @ConfigurationProperties(prefix = "stock-constant")
    public StockConfig stockConfig() {
        return new StockConfig();
    }
}
