package com.sinogale.redisson.config;

import lombok.extern.log4j.Log4j2;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties({RedissonProperties.class})
@ConditionalOnProperty(
        prefix = "redisson",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class RedissonAutoConfiguration {

    public static final String MODE_SINGLE = "single";

    public static final String MODE_CLUSTER = "cluster";

    private static final Logger log = LoggerFactory.getLogger(RedissonAutoConfiguration.class);

    @Bean(
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedissonProperties redisProperties) throws IOException {
        Config config = new Config();
        List<String> nodes = redisProperties.getNodes();
        if (Objects.equals("single", redisProperties.getMode())) {
            config.useSingleServer().setAddress(nodes.get(0)).setPassword(redisProperties.getPassword())
                    .setPingConnectionInterval(redisProperties.getPingConnectionInterval());
        } else {
            config.useClusterServers().addNodeAddress(Arrays.toString(nodes.stream().map(s -> "redis://" + s).toArray(Object[]::new))).setPingConnectionInterval(redisProperties.getPingConnectionInterval())
                    .setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    public CommandLineRunner redisRunner() {
        return (args) -> {
            log.info("==== redis loaded ===");
        };
    }
}
