package com.sinogale.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @ClassName HandleHttpErrorUtil
 * @Author duanchao
 * @Date 2021/7/21 11:50
 **/

public class HandleHttpErrorUtil {
    private static final Logger log = LoggerFactory.getLogger(HandleHttpErrorUtil.class);
    private static final Integer AUTH_ERROR_CODE = 403;

    private HandleHttpErrorUtil() {
    }

    public static void handleHttpError(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json");
        AuthResponse authResponse = new AuthResponse();
        authResponse.setCode(AUTH_ERROR_CODE);
        authResponse.setMsg(e.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(authResponse));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class AuthResponse implements Serializable {
        private Integer code;
        private String msg;
    }
}
