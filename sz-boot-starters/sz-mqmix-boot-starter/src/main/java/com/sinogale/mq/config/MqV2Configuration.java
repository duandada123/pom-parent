package com.sinogale.mq.config;

import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @ClassName MqV2Configuration
 * @Author duanchao
 * @Date 2021/7/19 13:29
 **/

@Configuration
@ConditionalOnProperty(
        prefix = "mq.rocketmq",
        name = {"v2enabled"},
        havingValue = "true"
)
public class MqV2Configuration {

    private static final Logger log = LoggerFactory.getLogger(MqV2Configuration.class);
    @Autowired
    private MqProperties mqProperties;

    @Bean(
            initMethod = "start",
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({ProducerBean.class})
    public ProducerBean v2Producer() {
        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(this.getMqProperties());
        log.info("v2 producer loaded");
        return producerBean;
    }

    private Properties getMqProperties() {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", this.mqProperties.getV2AccessKey());
        properties.setProperty("SecretKey", this.mqProperties.getV2SecretKey());
        properties.setProperty("ONSAddr", this.mqProperties.getOnsAddr());
        return properties;
    }

    @ConditionalOnMissingBean({OrderProducerBean.class})
    @Bean(
            initMethod = "start",
            destroyMethod = "shutdown"
    )
    public OrderProducerBean v2OrderProducer() {
        OrderProducerBean orderProducerBean = new OrderProducerBean();
        orderProducerBean.setProperties(this.getMqProperties());
        return orderProducerBean;
    }
}
