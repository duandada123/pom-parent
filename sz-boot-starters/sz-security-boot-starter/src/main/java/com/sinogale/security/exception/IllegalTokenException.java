package com.sinogale.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName IllegalTokenException
 * @Author duanchao
 * @Date 2021/7/21 12:02
 **/

public class IllegalTokenException extends AuthenticationException {

    public IllegalTokenException(String msg) {
        super(msg);
    }
}
