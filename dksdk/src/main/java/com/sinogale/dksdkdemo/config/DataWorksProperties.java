package com.sinogale.dksdkdemo.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "dataworks")
public class DataWorksProperties {
    private String endpoint;
    private String regionId;
    private Long projectId;
    private String accessKeyId;
    private String accessKeySecret;
    private String bizdateZone;
    private int callTimeoutSeconds;
    private int maxNodePages;
    private String projectEnv = "PROD";
}
