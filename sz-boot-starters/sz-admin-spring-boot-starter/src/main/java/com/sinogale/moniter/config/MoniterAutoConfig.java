package com.sinogale.moniter.config;

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

    @Bean
    public CommandLineRunner nacosLogger() {
        return (args) -> {
            System.out.println("==== SZKJ admin loaded  ^ __ ^====");
        };
    }
}
