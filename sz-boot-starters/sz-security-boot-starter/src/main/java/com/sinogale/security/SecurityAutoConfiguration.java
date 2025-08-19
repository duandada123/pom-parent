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

/**
 * @ClassName SecurityAutoConfiguration
 * @Author duanchao
 * @Date 2021/7/21 11:26
 **/
@Configuration
@ConditionalOnProperty(
        prefix = "dc.security",
        name = {"enable"},
        havingValue = "true"
)
public class SecurityAutoConfiguration {
    @Autowired
    private LoginConfigProperties properties;

    public SecurityAutoConfiguration() {
    }

    @Bean
    @ConditionalOnMissingBean({JwtUtil.class})
    public JwtUtil jwtUtil() {
        return new JwtUtil(this.properties.getSecret(), this.properties.getExpired());
    }

    @Configuration
    @ComponentScan({"com.sinogale.security.base", "com.sinogale.security.config"})
    @Import({SecurityConfig.class})
    public class AdminSecurityConfig {
        public AdminSecurityConfig() {
        }
    }
}
