package com.sinogale.security.base;


import com.sinogale.common.constants.SZPlatform;
import com.sinogale.security.config.AuthErrorMsg;
import com.sinogale.security.exception.IllegalTokenException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class BaseAuthenticationProvider {
    private SystemExpiredProperties properties;
    private RedisTemplate<String, String> redisTemplate;

    public BaseAuthenticationProvider(SystemExpiredProperties properties, RedisTemplate<String, String> redisTemplate) {
        this.properties = properties;
        this.redisTemplate = redisTemplate;
    }

    public Integer getExpired(SZPlatform platform) {
        return Objects.equals(SZPlatform.AUTH, platform) ? this.properties.getAdminSysHours() : 24;
    }

    public void saveLoginToken(String key, SZPlatform platform, String token, boolean forLogin) {
        if (this.redisTemplate.hasKey(key) && !forLogin) {
            throw new IllegalTokenException(AuthErrorMsg.hasUserOnline.getName());
        } else {
            this.redisTemplate.opsForValue().set(key, token);
            Integer expireInterval = this.getExpired(platform);
            this.redisTemplate.expire(key, (long)expireInterval, TimeUnit.HOURS);
        }
    }
}
