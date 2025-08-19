package com.sinogale.security.base;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName JwtAuthToken
 * @Author duanchao
 * @Date 2021/7/21 13:35
 **/

public class JwtAuthToken implements Authentication {

    private final String token;

    private String userAgent;

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public JwtAuthToken(String token) {
        this.token = token;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Object getCredentials() {
        return this.token;
    }

    public Object getDetails() {
        return null;
    }

    public Object getPrincipal() {
        return null;
    }

    public boolean isAuthenticated() {
        return false;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    public String getName() {
        return null;
    }
}
