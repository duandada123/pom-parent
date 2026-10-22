package com.sinogale.dwsdk.config;

import com.sinogale.dwsdk.DwSdkScene;
import com.sinogale.dwsdk.DwSdkSceneWrapper;
import com.sinogale.dwsdk.DwSdkClient;
import com.sinogale.dwsdk.sdk.DataWorksGateway;
import com.sinogale.dwsdk.service.DataWorksService;
import com.sinogale.dwsdk.service.DataWorksLogService;
import com.aliyun.sdk.service.dataworks_public20240518.AsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import javax.validation.Validator;

import java.util.List;

@Configuration
@AutoConfigureAfter(ValidationAutoConfiguration.class)
@ConditionalOnClass(AsyncClient.class)
@EnableConfigurationProperties(DwSdkProperties.class)
@ConditionalOnProperty(
        prefix = "dwsdk",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DwSdkAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DwSdkAutoConfiguration.class);

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public DataWorksClientProvider dataWorksClientProvider(DwSdkProperties properties) {
        return new DataWorksClientProvider(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataWorksGateway dataWorksGateway(DataWorksClientProvider provider, DwSdkProperties properties) {
        return new DataWorksGateway(provider, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataWorksService dataWorksService(DataWorksGateway gateway, DwSdkProperties properties) {
        return new DataWorksService(gateway, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public DataWorksLogService dataWorksLogService(DataWorksGateway gateway, DataWorksService files,
                                                 DwSdkProperties properties) {
        return new DataWorksLogService(gateway, files, properties);
    }

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public LocalValidatorFactoryBean dwSdkValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public DwSdkClient dwSdkClient(DataWorksService files, DataWorksLogService logs, Validator validator) {
        return new DwSdkClient(files, logs, validator);
    }

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
