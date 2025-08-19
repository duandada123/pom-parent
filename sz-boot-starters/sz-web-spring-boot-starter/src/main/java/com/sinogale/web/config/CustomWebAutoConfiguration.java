package com.sinogale.web.config;

/**
 * @ClassName CustomWebAutoConfiguration
 * @Author duanchao
 * @Date 2022/4/3 20:43
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@ImportAutoConfiguration({SwaggerPlusConfiguration.class, CustomMvcConfiguration.class})
public class CustomWebAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CustomWebAutoConfiguration.class);

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
            log.info("==== swagger2 loaded ====");
        };
    }
}
