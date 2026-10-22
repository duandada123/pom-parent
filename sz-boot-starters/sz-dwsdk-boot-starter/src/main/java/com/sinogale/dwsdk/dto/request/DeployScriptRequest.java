package com.sinogale.dwsdk.dto.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@ApiModel(value = "DeployScriptRequest", description = "发布及状态查询请求")
@Getter
@Setter
public class DeployScriptRequest {
    @Positive
    @ApiModelProperty(value = "DataWorks 工作空间 ID；省略时使用 DATAWORKS_PROJECT_ID", example = "10000")
    private Long projectId;
    @Positive
    @ApiModelProperty(value = "实际发布时必填；checkOnly=true 时可省略", example = "1000001")
    private Long fileId;
    @NotNull @Positive
    @ApiModelProperty(value = "实际发布传提交包 ID；只查询时可传提交包或生产发布包 ID", example = "3000001", required = true)
    private Long deploymentId;

    @ApiModelProperty(value = "是否只查询状态；默认 false，true 时不会执行发布", example = "false")
    private boolean checkOnly = false;

    @ApiModelProperty(value = "本次提交或发布说明", example = "SDK 示例操作")
    private String comment;

    @AssertTrue(message = "发布时必须提供 fileId；仅查询状态时可省略")
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isFileRequiredSatisfied() { return checkOnly || fileId != null; }

}
