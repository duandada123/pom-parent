package com.sinogale.dksdkdemo.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class DataWorksApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String requestId;

    public DataWorksApiException(HttpStatus status, String code, String message, String requestId) {
        super(message);
        this.status = status;
        this.code = code;
        this.requestId = requestId;
    }
}
