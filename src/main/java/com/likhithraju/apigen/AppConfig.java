package com.likhithraju.apigen;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.likhithraju.apigen.configs.ApiConfigModel;

@Configuration
public class AppConfig {

    @Bean
    public ApiConfigModel apiConfigModel() {
        return new ApiConfigModel(); 
    }
}

