package com.sinogale.dksdkdemo.dto.response;

import lombok.Getter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ApiResponse", description = "统一接口响应")
@Getter
public class ApiResponse<T> {
    @ApiModelProperty(value = "本次接口是否正常处理；不代表异步提交/发布已完成", example = "true")
    private final boolean success;
    @ApiModelProperty(value = "接口结果码", example = "OK")
    private final String code;
    @ApiModelProperty(value = "处理结果说明")
    private final String message;
    @ApiModelProperty(value = "阿里云请求追踪 ID，用于排查问题", example = "aliyun-request-id")
    private final String requestId;
    @ApiModelProperty(value = "具体业务结果")
    private final T data;

    private ApiResponse(boolean success, String code, String message, String requestId, T data) {
        this.success = success; this.code = code; this.message = message;
        this.requestId = requestId; this.data = data;
    }
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", "请求处理成功，业务状态请查看 data", null, data);
    }
    public static ApiResponse<Void> error(String code, String message, String requestId) {
        return new ApiResponse<>(false, code, message, requestId, null);
    }
}
