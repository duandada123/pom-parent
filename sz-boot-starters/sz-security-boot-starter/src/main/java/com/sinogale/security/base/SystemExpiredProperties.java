package com.sinogale.security.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SystemExpiredProperties
 * @Author duanchao
 * @Date 2021/7/21 11:57
 **/


@ConfigurationProperties(
        prefix = "dc.security.token.expired"
)
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemExpiredProperties {
    private Integer adminSysHours = 24;




}
