package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CreateScriptResponse", description = "创建脚本结果")
public class CreateScriptResponse {
    @ApiModelProperty(value = "新建文件 ID，可用于后续提交操作", example = "1000001")
    private Long fileId;

    @ApiModelProperty(value = "创建文件接口返回的请求追踪 ID，用于排查上游错误")
    private String requestId;

}
