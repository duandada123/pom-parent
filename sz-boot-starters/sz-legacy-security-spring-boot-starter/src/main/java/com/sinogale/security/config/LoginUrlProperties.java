package com.sinogale.security.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName LoginUrlProperties
 * @Author duanchao
 * @Date 2021/7/21 12:06
 **/
@Component
@ConfigurationProperties(
        prefix = "sz.security.urls"
)
@Data
public class LoginUrlProperties {

    private String adminPassUrl;

    private List<String> unAuthUrls;


}
