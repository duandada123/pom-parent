package com.sinogale.dksdkdemo.config;

import com.aliyun.dataworks_20240902.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;



@Component
public class DataWorksClientConfig {

    private  DataWorksProperties properties;

    public DataWorksClientConfig(DataWorksProperties properties) {
        this.properties = properties;
    }
    @Bean
    public Client createClient() throws Exception {
            Client client = new Client(new Config()
                    .setEndpoint(properties.getEndpoint())
                    .setRegionId(properties.getRegionId())
                    .setAccessKeyId(properties.getAccessKeyId())
                    .setAccessKeySecret(properties.getAccessKeySecret()));
        return client;
        }

    }
