package com.sinogale.security.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SystemExpiredProperties
 * @Author duanchao
 * @Date 2021/7/21 11:57
 **/

@ConfigurationProperties(prefix = "sz.security.token.expired")
@Component
@Data
public class SystemExpiredProperties {

    private Integer adminSysHours = Integer.valueOf(24);


}

