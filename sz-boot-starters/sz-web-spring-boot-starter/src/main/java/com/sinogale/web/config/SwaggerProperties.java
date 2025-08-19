package com.sinogale.web.config;

/**
 * @ClassName SwaggerProperties
 * @Author duanchao
 * @Date 2022/4/3 20:44
 **/

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sz.web")
@Data
public class SwaggerProperties {
    private boolean enable;
    private String description = "description";
    private String title = "title";
    private String basePackage = "";
    private String version = "version 1.0.0";

}
