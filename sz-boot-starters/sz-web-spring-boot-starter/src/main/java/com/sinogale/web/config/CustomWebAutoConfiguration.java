package com.sinogale.web.config;

/**
 * @ClassName CustomWebAutoConfiguration
 * @Author duanchao
 * @Date 2022/4/3 20:43
 **/

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@ImportAutoConfiguration({SwaggerPlusConfiguration.class, CustomMvcConfiguration.class})
public class CustomWebAutoConfiguration {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);
        filter.setAfterMessagePrefix("REQUEST : ");
        return filter;
    }

    @Bean
    public CommandLineRunner webRunner(){
        return (args) -> {
            System.out.println("==== swagger2 loaded ====");
        };
    }
}
