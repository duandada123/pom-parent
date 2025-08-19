package com.sinogale.security.base;

import com.sinogale.common.constants.SZPlatform;

import java.util.Objects;

public abstract class BaseAuthenticationProvider {

    private SystemExpiredProperties properties;

    public BaseAuthenticationProvider(SystemExpiredProperties properties) {
        this.properties = properties;
    }

    public Integer getExpired(SZPlatform platform) {
        if (Objects.equals(SZPlatform.AUTH, platform))
            return this.properties.getAdminSysHours();
        return Integer.valueOf(24);
    }
}
