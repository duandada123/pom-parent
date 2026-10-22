package com.sinogale.dksdkdemo.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "CreateScriptResponse", description = "创建脚本结果")
@Getter
@Setter
public class CreateScriptResponse {

    @ApiModelProperty(value = "DataWorks 文件 ID", example = "1000001")
    private Long fileId;

    @ApiModelProperty(value = "阿里云请求追踪 ID，用于排查问题", example = "aliyun-request-id")
    private String requestId;

}
