package com.sinogale.security.base;

import com.sinogale.common.constants.SZPlatform;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BaseUsernamePasswordToken  extends AbstractAuthenticationToken {
    private String username;
    private String password;
    private boolean forceLogin;
    private SZPlatform platform;

    public BaseUsernamePasswordToken(Collection<? extends GrantedAuthority> authorities, String username, String password, boolean forceLogin, SZPlatform platform) {
        super(authorities);
        this.username = username;
        this.password = password;
        this.forceLogin = forceLogin;
        this.platform = platform;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isForceLogin() {
        return this.forceLogin;
    }

    public SZPlatform getPlatform() {
        return this.platform;
    }
}
