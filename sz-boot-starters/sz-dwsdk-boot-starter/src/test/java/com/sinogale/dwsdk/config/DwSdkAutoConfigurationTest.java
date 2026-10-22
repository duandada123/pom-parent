package com.sinogale.dwsdk.config;

import com.sinogale.dwsdk.DwSdkSceneWrapper;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class DwSdkAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DwSdkAutoConfiguration.class));

    @Test
    public void shouldCreateSceneWrapperByDefault() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DwSdkSceneWrapper.class);
            assertThat(context.getBean(DwSdkSceneWrapper.class).size()).isZero();
        });
    }

    @Test
    public void shouldBackOffWhenDisabled() {
        contextRunner
                .withPropertyValues("dwsdk.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(DwSdkSceneWrapper.class));
    }
}
