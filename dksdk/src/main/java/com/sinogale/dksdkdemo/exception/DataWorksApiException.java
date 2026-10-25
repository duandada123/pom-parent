package com.sinogale.dksdkdemo.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataWorksApiException extends RuntimeException {
    private final String code;
    private final String requestId;

    public DataWorksApiException(String code, String message, String requestId) {
        super(message);
        this.code = code;
        this.requestId = requestId;
    }
}
