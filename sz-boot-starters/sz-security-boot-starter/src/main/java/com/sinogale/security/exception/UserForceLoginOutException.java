package com.sinogale.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @ClassName UserForceLoginOutException
 * @Author duanchao
 * @Date 2021/7/21 13:38
 **/

public class UserForceLoginOutException  extends AuthenticationException {
    public UserForceLoginOutException(String msg) {
        super(msg);
    }
}

