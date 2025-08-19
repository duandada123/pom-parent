package com.sinogale.nacos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GxNacosAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GxNacosAutoConfiguration.class);

    @Bean
    public CommandLineRunner nacosLogger() {
        return (args) -> {
            log.info("==== SZKJ NACOS loaded  ^ __ ^====");
        };
    }
}
