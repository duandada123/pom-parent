package com.sinogale.security.base;

import java.util.Objects;

import com.sinogale.security.base.platform.UserContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider extends BaseAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    private final UserContextAware userContextAware;

    public JwtAuthenticationProvider(SystemExpiredProperties properties, UserContextAware userContextAware) {
        super(properties);
        this.userContextAware = userContextAware;
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String)authentication.getCredentials();
        BaseJwtUser jwtUser = null;
        if (Objects.isNull(this.userContextAware))
            return authentication;
        jwtUser = this.userContextAware.processToken(token);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return authToken;
    }

    public boolean supports(Class<?> authentication) {
        return JwtAuthToken.class.equals(authentication);
    }
}
