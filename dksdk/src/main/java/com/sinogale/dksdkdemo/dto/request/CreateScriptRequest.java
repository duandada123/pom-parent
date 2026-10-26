package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "CreateScriptRequest", description = "创建脚本请求")
public class CreateScriptRequest {
    @ApiModelProperty(value = "文件名称", example = "dksdk_test_demo", required = true)
    @NotBlank
    private String fileName;

    @ApiModelProperty(value = "DataWorks 文件代码类型；按目标平台支持的节点类型填写", example = "1221", required = true)
    @NotNull @Positive
    private Integer fileType;

    @ApiModelProperty(value = "目标工作空间中已存在的文件目录路径，使用 / 分隔；以平台实际路径为准", example = "业务流程/123/MaxCompute", required = true)
    @NotBlank
    private String fileFolderPath;

    @ApiModelProperty(value = "脚本代码内容", example = "print('hello DataWorks')", required = true)
    @NotBlank
    private String content;

    @ApiModelProperty(value = "文件描述")
    private String fileDescription;

    @ApiModelProperty(value = "文件责任人用户 UID")
    private String owner;

    @ApiModelProperty(value = "执行节点使用的数据源连接名称")
    private String connectionName;

    @ApiModelProperty(value = "DataWorks 调度 cron 表达式", example = "00 00 01 * * ?")
    private String cronExpress;

    @ApiModelProperty(value = "调度周期类型", example = "DAY")
    private String cycleType;

    @ApiModelProperty(value = "跨周期依赖节点列表，格式以目标平台接口约定为准")
    private String dependentNodeIdList;

    @ApiModelProperty(value = "跨周期依赖类型，取值以目标平台接口约定为准")
    private String dependentType;

    @ApiModelProperty(value = "调度参数，多个参数用空格分隔", example = "bizdate=$bizdate")
    private String paraValue;

    @ApiModelProperty(value = "上游节点输出名称，多个名称用英文逗号分隔；公司环境创建接口必填，不能填写业务流程名", example = "workspace_root")
    private String inputList;

    @ApiModelProperty(value = "调度资源组 ID")
    private Long resourceGroupId;

    @ApiModelProperty(value = "调度资源组标识符")
    private String resourceGroupIdentifier;

    @ApiModelProperty(value = "调度类型，按目标平台支持的类型填写", example = "NORMAL")
    private String schedulerType;

    @ApiModelProperty(value = "调度生效开始时间，毫秒时间戳")
    private Long startEffectDate;

    @ApiModelProperty(value = "调度生效结束时间，毫秒时间戳")
    private Long endEffectDate;

    @ApiModelProperty(value = "是否自动解析代码依赖", example = "false")
    private Boolean autoParsing;

    @ApiModelProperty(value = "执行失败时的自动重跑次数", example = "3")
    private Integer autoRerunTimes;

    @ApiModelProperty(value = "执行失败后的自动重跑间隔，单位毫秒", example = "120000")
    private Integer autoRerunIntervalMillis;

    @ApiModelProperty(value = "重跑策略，按目标平台支持的策略填写", example = "ALL_ALLOWED")
    private String rerunMode;

    @ApiModelProperty(value = "是否暂停调度", example = "false")
    private Boolean stop;

}
