package com.sinogale.security.base;

import com.google.common.collect.Iterables;
import com.sinogale.security.base.platform.UserContextAware;
import com.sinogale.security.config.AuthErrorMsg;
import com.sinogale.security.exception.TokenExpiredException;
import com.sinogale.security.exception.UserForceLoginOutException;
import com.sinogale.security.util.AuthHelper;
import com.sinogale.security.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationProvider extends BaseAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final List<UserContextAware> userContextAwareList;

    public JwtAuthenticationProvider(JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate, SystemExpiredProperties properties, List<UserContextAware> userContextAwareList) {
        super(properties, redisTemplate);
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.userContextAwareList = userContextAwareList;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String)authentication.getCredentials();
        TokenInfo tokenInfo = this.jwtUtil.getTokenInfo(token);
        BaseJwtUser jwtUser = null;
        if (Iterables.isEmpty(this.userContextAwareList)) {
            return authentication;
        } else {
            Iterator iterator = this.userContextAwareList.iterator();

            UserContextAware pta;
            do {
                if (!iterator.hasNext()) {
                    return authentication;
                }

                pta = (UserContextAware)iterator.next();
            } while(!pta.supports(tokenInfo));

            jwtUser = pta.getSessionInfo(tokenInfo);
            this.validateSingleLogin(jwtUser, token);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return authToken;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthToken.class.equals(authentication);
    }

    private void validateSingleLogin(BaseJwtUser user, String token) {
        String key = AuthHelper.getLoginRedisKey(user);
        if (!this.redisTemplate.hasKey(key)) {
            throw new TokenExpiredException(AuthErrorMsg.tokenInvalid.getName());
        } else if (!this.redisTemplate.opsForValue().get(key).equals(token)) {
            throw new UserForceLoginOutException(AuthErrorMsg.forceLoginOut.getName());
        } else {
            this.redisTemplate.opsForValue().set(key, token);
            Integer expired = this.getExpired(user.getPlatform());
            this.redisTemplate.expire(key, (long)expired, TimeUnit.HOURS);
        }
    }
}
