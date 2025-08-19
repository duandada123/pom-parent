package com.sinogale.redisson.config;

/**
 * @ClassName RedissonProperties
 * @Author duanchao
 * @Date 2022/4/3 20:28
 **/

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redisson")
@Data
public class RedissonProperties {
    private String mode = "single";
    private List<String> nodes;
    private String password;
    private boolean enabled;
    private Integer pingConnectionInterval;

}
