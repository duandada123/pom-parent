package com.sinogale.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName TokenExpiredException
 * @Author duanchao
 * @Date 2021/7/21 13:38
 **/

public class TokenExpiredException extends AuthenticationException {
    public TokenExpiredException(String msg) {
        super(msg);
    }
}
