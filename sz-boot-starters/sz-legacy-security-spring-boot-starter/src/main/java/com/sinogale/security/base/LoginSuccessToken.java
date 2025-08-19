package com.sinogale.security.base;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName LoginSuccessToken
 * @Author duanchao
 * @Date 2021/7/21 14:35
 **/

public class LoginSuccessToken extends AbstractAuthenticationToken {

    private final String token;

    private String username;

    public LoginSuccessToken(Collection<? extends GrantedAuthority> authorities, String token, String name) {
        super(authorities);
        this.token = token;
        this.username = name;
    }

    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.token;
    }

    public String getUsername() {
        return this.username;
    }
}