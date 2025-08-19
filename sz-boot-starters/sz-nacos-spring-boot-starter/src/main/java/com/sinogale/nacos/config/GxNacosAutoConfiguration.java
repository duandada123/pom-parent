package com.sinogale.nacos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GxNacosAutoConfiguration {

    @Bean
    public CommandLineRunner nacosLogger() {
        return (args) -> {
            System.out.println("==== SZKJ NACOS loaded  ^ __ ^====");
        };
    }
}
