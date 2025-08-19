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
import org.springframework.context.annotation.Primary;

import java.util.Properties;

/**
 * @ClassName MqV3Configuration
 * @Author duanchao
 * @Date 2021/7/19 13:29
 **/

@Configuration
@ConditionalOnProperty(
        prefix = "mq.rocketmq",
        name = {"v3enabled"},
        havingValue = "true"
)
public class MqV3Configuration {

    private static final Logger log = LoggerFactory.getLogger(MqV3Configuration.class);

    @Autowired
    private MqProperties mqProperties;

    @Bean(
            initMethod = "start",
            destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean({ProducerBean.class})
    @Primary
    public ProducerBean v3Producer() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(this.getMqProperties());
        log.info("V3 producer loaded");
        return producer;
    }

    private Properties getMqProperties() {
        Properties properties = new Properties();
        properties.setProperty("AccessKey", this.mqProperties.getV3AccessKey());
        properties.setProperty("SecretKey", this.mqProperties.getV3SecretKey());
        properties.setProperty("NAMESRV_ADDR", this.mqProperties.getNameSrv());
        properties.setProperty("GROUP_ID", this.mqProperties.getGroupId());
        return properties;
    }

    @ConditionalOnMissingBean({OrderProducerBean.class})
    @Bean(
            initMethod = "start",
            destroyMethod = "shutdown"
    )
    @Primary
    public OrderProducerBean v3OrderProducer() {
        OrderProducerBean orderProducerBean = new OrderProducerBean();
        orderProducerBean.setProperties(this.getMqProperties());
        return orderProducerBean;
    }
}
