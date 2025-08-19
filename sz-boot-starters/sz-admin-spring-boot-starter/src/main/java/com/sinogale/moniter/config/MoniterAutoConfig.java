package com.sinogale.moniter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MoniterAutoConfig
 * @Author duanchao
 * @Date 2022/8/11 10:01
 **/
@Configuration
public class MoniterAutoConfig {

    private static final Logger log = LoggerFactory.getLogger(MoniterAutoConfig.class);

    @Bean
    public CommandLineRunner nacosLogger() {
        return (args) -> {
            log.info("==== SZKJ admin loaded  ^ __ ^====");
        };
    }
}
