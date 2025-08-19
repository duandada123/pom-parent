package com.sinogale.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName MqProperties
 * @Author duanchao
 * @Date 2021/7/19 13:26
 **/
@Data
@ConfigurationProperties(prefix = "mq.rocketmq")
public class MqProperties {

    private String v2AccessKey;

    private String v2SecretKey;

    private String v3AccessKey;

    private String v3SecretKey;

    private String nameSrv;

    private String groupId;

    private boolean v3enabled;

    private String onsAddr;

    private boolean v2enabled;

    private boolean enable;

}
