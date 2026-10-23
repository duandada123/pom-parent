package com.sinogale.dksdkdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

@ApiModel(value = "QueryLogsRequest", description = "日志查询请求")
@Getter
@Setter
public class QueryLogsRequest {
    @Positive
    @ApiModelProperty(value = "DataWorks 工作空间 ID；省略时使用 DATAWORKS_PROJECT_ID", example = "10000")
    private Long projectId;
    @Positive @JsonAlias("nodeId")
    @ApiModelProperty(value = "调度节点 ID，与 fileName、instanceId 三选一；字段名 taskId 为旧接口兼容", example = "2000001")
    private Long taskId;

    @ApiModelProperty(value = "文件名称；日志查询时要求唯一精确匹配", example = "python_demo")
    private String fileName;
    @Positive
    @ApiModelProperty(value = "任务实例 ID，与 taskId、fileName 三选一", example = "9000001")
    private Long instanceId;
    @Min(1)
    @ApiModelProperty(value = "实例运行次数，从 1 开始；请求时仅用于 instanceId 查询，不传取最近一次", example = "1")
    private Integer runNumber;

    @ApiModelProperty(value = "业务日期 yyyy-MM-dd；按任务查询必填，按实例查询可省略", example = "2026-10-21", dataType = "java.lang.String")
    private LocalDate bizdate;
    @NotBlank @Pattern(regexp = "(?i)Prod|Dev")
    @ApiModelProperty(value = "项目环境，默认 Prod，兼容大小写", example = "Prod", allowableValues = "Prod,Dev,PROD,DEV")
    private String projectEnv = "Prod";

    @ApiModelProperty(value = "兼容旧接口；20240902 SDK 的 ListNodes 不再支持此筛选条件")
    private String containerId;

    @ApiModelProperty(value = "兼容旧接口；20240902 SDK 的 ListNodes 不再支持此筛选条件")
    private String scene;
    @Min(1) @Max(1000000)
    @ApiModelProperty(value = "起始页码，1～1000000，默认 1", example = "1")
    private int pageNumber = 1;
    @Min(1) @Max(100)
    @ApiModelProperty(value = "每页实例数量，1～100，默认 20", example = "20")
    private int pageSize = 20;
    @Min(1) @Max(5)
    @ApiModelProperty(value = "本次最多读取页数，1～5，默认 1", example = "1")
    private int maxPages = 1;

    @AssertTrue(message = "nodeId（兼容字段 taskId）、fileName、instanceId 必须且只能选择一个")
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isSelectorValid() {
        return (taskId == null ? 0 : 1) + (fileName == null || fileName.trim().isEmpty() ? 0 : 1)
                + (instanceId == null ? 0 : 1) == 1;
    }

    @AssertTrue(message = "按任务查询日志必须提供 bizdate（yyyy-MM-dd）")
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isBizdateValid() { return instanceId != null || bizdate != null; }

    @AssertTrue(message = "runNumber 仅用于按 instanceId 查询")
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean isRunNumberValid() { return runNumber == null || instanceId != null; }

}
