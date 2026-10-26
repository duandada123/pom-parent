package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "RunScriptRequest", description = "创建周期任务补数据工作流（RunCycleDagNodes）")
public class RunScriptRequest {

    @ApiModelProperty(value = "补数据起始节点 ID，不是 fileId", example = "3000001", required = true)
    @NotNull
    @Positive
    private Long rootNodeId;

    @ApiModelProperty(value = "补数据起始业务日期，格式必须为 yyyy-MM-dd 00:00:00", example = "2026-10-01 00:00:00", required = true)
    @NotBlank
    private String startBizDate;

    @ApiModelProperty(value = "补数据结束业务日期，格式必须为 yyyy-MM-dd 00:00:00，且不能早于起始日期", example = "2026-10-02 00:00:00", required = true)
    @NotBlank
    private String endBizDate;

    @ApiModelProperty(value = "补数据工作流名称；名称不是幂等键", example = "backfill_demo", required = true)
    @NotBlank
    private String name;

    @ApiModelProperty(value = "工作空间环境 DEV/PROD；省略时使用 dataworks.project-env", example = "PROD")
    @Pattern(regexp = "(?i)DEV|PROD")
    private String projectEnv;

    @ApiModelProperty(value = "是否允许多个业务日期的任务并行运行", example = "false", required = true)
    @NotNull
    private Boolean parallelism = Boolean.FALSE;

    @ApiModelProperty(value = "需要补数据的节点 ID，多个 ID 使用英文逗号分隔", example = "3000001,3000002", required = true)
    @NotBlank
    @Pattern(regexp = "[1-9]\\d*(,[1-9]\\d*)*", message = "includeNodeIds 必须是英文逗号分隔的正整数")
    private String includeNodeIds;

    @ApiModelProperty(value = "无需执行脚本的节点 ID，多个 ID 使用英文逗号分隔；这些节点将生成空跑实例", example = "3000003")
    @Pattern(regexp = "[1-9]\\d*(,[1-9]\\d*)*", message = "excludeNodeIds 必须是英文逗号分隔的正整数")
    private String excludeNodeIds;

    @ApiModelProperty(value = "小时调度任务的开始时间，格式 HH:mm:ss；与 bizEndTime 同时传入", example = "00:00:00")
    @Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d")
    private String bizBeginTime;

    @ApiModelProperty(value = "小时调度任务的结束时间，格式 HH:mm:ss；与 bizBeginTime 同时传入", example = "23:00:00")
    @Pattern(regexp = "(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d")
    private String bizEndTime;

    @ApiModelProperty(value = "可选节点参数 JSON 字符串，Key 为节点 ID，Value 为参数实际值", example = "{\"3000001\":\"bizdate=20261001\"}")
    private String nodeParams;
}
