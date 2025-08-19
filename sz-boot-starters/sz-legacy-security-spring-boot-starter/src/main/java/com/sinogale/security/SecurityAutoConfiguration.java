package com.sinogale.security;

import com.sinogale.security.auto.SecurityConfig;
import com.sinogale.security.config.LoginConfigProperties;
import com.sinogale.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "sz.security", name = {"enable"}, havingValue = "true")
public class SecurityAutoConfiguration {

    @Autowired
    private LoginConfigProperties properties;

    @Configuration
    @ComponentScan({"com.sinogale.security.base", "com.sinogale.security.config"})
    @Import({SecurityConfig.class})
    public class AdminSecurityConfig {

    }

    @Bean
    @ConditionalOnMissingBean({JwtUtil.class})
    public JwtUtil jwtUtil() {
        return new JwtUtil(this.properties.getSecret(), this.properties.getExpired());
    }
}