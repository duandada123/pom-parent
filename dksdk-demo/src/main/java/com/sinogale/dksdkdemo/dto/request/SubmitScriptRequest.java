package com.sinogale.dksdkdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;

@ApiModel(value = "SubmitScriptRequest", description = "提交脚本请求")
@Getter
@Setter
public class SubmitScriptRequest {
    @Positive
    @ApiModelProperty(value = "DataWorks 工作空间 ID；省略时使用 DATAWORKS_PROJECT_ID", example = "10000")
    private Long projectId;
    @NotNull @Positive
    @ApiModelProperty(value = "DataWorks 文件 ID", example = "1000001", required = true)
    private Long fileId;

    @ApiModelProperty(value = "本次提交或发布说明", example = "SDK 示例操作")
    private String comment;

}
