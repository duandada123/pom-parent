package com.sinogale.dksdkdemo.exception;

import com.sinogale.dksdkdemo.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(DataWorksException.class)
    public ResponseEntity<ApiResponse<Void>> dataWorks(DataWorksException e) {
        // 不打印脚本、凭据或 SDK 请求内容。
        LOG.warn("DataWorks request failed: code={}, requestId={}", e.getCode(), e.getRequestId());
        return ResponseEntity.status(e.getHttpStatus()).body(
                ApiResponse.error(e.getCode(), e.getMessage(), e.getRequestId()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_REQUEST", message, null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> malformed(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                "INVALID_JSON", "JSON 格式或字段类型错误，日期格式为 yyyy-MM-dd", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> unexpected(Exception e) {
        LOG.error("Unexpected request failure: {}", e.getClass().getName());
        return ResponseEntity.status(500).body(ApiResponse.error("INTERNAL_ERROR", "服务器内部错误", null));
    }
}
