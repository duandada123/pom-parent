package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "QueryExecutionRequest", description = "按 runScript 返回的 dagIds 中某个 dagId 分页查询执行实例状态")
public class QueryExecutionRequest {

    @ApiModelProperty(value = "runScript 返回的 dagIds 中某个工作流 ID，不是 instanceId", example = "4000001", required = true)
    @NotNull
    @Positive
    private Long dagId;

    @ApiModelProperty(value = "运行环境 DEV/PROD；省略时使用 dataworks.project-env", example = "PROD")
    @Pattern(regexp = "(?i)DEV|PROD")
    private String projectEnv;

    @ApiModelProperty(value = "页码，从 1 开始", example = "1", required = true)
    @NotNull
    @Min(1)
    @Max(1000000)
    private Integer pageNumber = 1;

    @ApiModelProperty(value = "每页数量，范围 1～100", example = "20", required = true)
    @NotNull
    @Min(1)
    @Max(100)
    private Integer pageSize = 20;
}
