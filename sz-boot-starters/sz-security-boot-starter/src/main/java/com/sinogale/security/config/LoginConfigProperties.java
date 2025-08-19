package com.sinogale.security.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoginConfigProperties
 * @Author duanchao
 * @Date 2021/7/21 14:41
 **/
@Component
@ConfigurationProperties(
        prefix = "dc.security"
)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginConfigProperties {
    private String secret;
    private Long expired;


}
