package com.sinogale.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName MethodNotSupportException
 * @Author duanchao
 * @Date 2021/7/21 14:29
 **/

public class MethodNotSupportException extends AuthenticationException {
    public MethodNotSupportException(String msg) {
        super(msg);
    }
}

