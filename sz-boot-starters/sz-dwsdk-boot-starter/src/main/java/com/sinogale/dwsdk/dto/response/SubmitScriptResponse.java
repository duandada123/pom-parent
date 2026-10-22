package com.sinogale.dwsdk.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "SubmitScriptResponse", description = "提交脚本结果")
@Getter
@Setter
public class SubmitScriptResponse {

    @ApiModelProperty(value = "DataWorks 文件 ID", example = "1000001")
    private Long fileId;

    @ApiModelProperty(value = "本次提交包 ID；使用此 ID 检查提交状态", example = "3000001")
    private Long deploymentId;

    @ApiModelProperty(value = "业务处理状态", example = "SUBMIT_ACCEPTED", allowableValues = "SUBMIT_ACCEPTED")
    private String state = "SUBMIT_ACCEPTED";

    @ApiModelProperty(value = "阿里云请求追踪 ID，用于排查问题", example = "aliyun-request-id")
    private String requestId;

}
