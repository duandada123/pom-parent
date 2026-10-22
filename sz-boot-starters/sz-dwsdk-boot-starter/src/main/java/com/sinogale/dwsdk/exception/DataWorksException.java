package com.sinogale.dwsdk.exception;

import lombok.Getter;

@Getter
public class DataWorksException extends RuntimeException {
    private final int httpStatus;
    private final String code;
    private final String requestId;

    public DataWorksException(int httpStatus, String code, String message, String requestId) {
        super(message);
        this.httpStatus = httpStatus; this.code = code; this.requestId = requestId;
    }
}
