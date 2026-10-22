package com.sinogale.dwsdk.config;

import com.sinogale.dwsdk.DwSdkScene;
import com.sinogale.dwsdk.DwSdkSceneWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(DwSdkProperties.class)
@ConditionalOnProperty(
        prefix = "dwsdk",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DwSdkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DwSdkAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public DwSdkSceneWrapper dwSdkSceneWrapper(List<DwSdkScene<?, ?>> scenes) {
        return new DwSdkSceneWrapper(scenes);
    }

    @Bean
    public CommandLineRunner dwSdkRunner(DwSdkSceneWrapper sceneWrapper) {
        return args -> log.info("==== DW SDK loaded; registered scenes: {} ====", sceneWrapper.getSceneNames());
    }
}
