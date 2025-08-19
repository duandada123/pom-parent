package com.sinogale.mq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MqAutoConfiguration
 * @Author duanchao
 * @Date 2021/7/19 13:07
 **/

@Configuration
@ConditionalOnProperty(
        prefix = "mq.rocketmq",
        name = {"enable"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties({MqProperties.class})
@ImportAutoConfiguration({MqV3Configuration.class, MqV2Configuration.class})
public class MqAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MqAutoConfiguration.class);

    @Bean()
    public CommandLineRunner mqRunner() {
        return (args) -> {
            log.info("rocket MQ loaded");
        };
    }

}
