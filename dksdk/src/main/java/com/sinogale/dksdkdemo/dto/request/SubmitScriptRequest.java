package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "SubmitScriptRequest", description = "提交脚本请求")
public class SubmitScriptRequest {
    @ApiModelProperty(value = "待提交文件 ID，由创建文件接口返回", example = "1000001", required = true)
    @NotNull @Positive
    private Long fileId;

}
