package com.sinogale.dksdkdemo.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.*;

@Component
@Validated
@ConfigurationProperties(prefix = "dataworks")
@Getter
@Setter
public class DataWorksProperties {

    private String endpoint = "";

    private String regionId = "cn-hangzhou";

    private Long projectId;

    private String accessKeyId = "";

    private String accessKeySecret = "";

    private String securityToken = "";

    private String bizdateZone = "Asia/Shanghai";
    @Min(1) @Max(120)
    private int callTimeoutSeconds = 20;
    @Min(1) @Max(100)
    private int maxNodePages = 20;

}
