package com.sinogale.dksdkdemo.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "InstanceLogResponse", description = "单次实例运行日志")
@Getter
@Setter
public class InstanceLogResponse {

    @ApiModelProperty(value = "任务实例 ID", example = "9000001")
    private Long instanceId;

    @ApiModelProperty(value = "调度节点 ID（兼容字段名 taskId）", example = "2000001")
    private Long taskId;

    @ApiModelProperty(value = "调度任务名称", example = "python_demo")
    private String taskName;

    @ApiModelProperty(value = "实例重跑次数；20240902 SDK 查询日志时不支持按该值筛选", example = "1")
    private Integer runNumber;

    @ApiModelProperty(value = "实例运行状态", example = "Success")
    private String status;

    @ApiModelProperty(value = "业务日期，毫秒时间戳", example = "1792512000000")
    private Long bizdate;

    @ApiModelProperty(value = "实例开始时间，毫秒时间戳；未开始时可为 null")
    private Long startedTime;

    @ApiModelProperty(value = "实例结束时间，毫秒时间戳；未结束时可为 null")
    private Long finishedTime;

    @ApiModelProperty(value = "实例运行日志文本；尚未产生时可能为空", example = "hello DataWorks")
    private String logContent;

    @ApiModelProperty(value = "该条日志是否读取成功", example = "true")
    private boolean logSuccess;

    @ApiModelProperty(value = "单条日志读取失败的错误码；成功时为 null")
    private String errorCode;

    @ApiModelProperty(value = "单条日志读取失败的说明；成功时为 null")
    private String errorMessage;

    @ApiModelProperty(value = "阿里云请求追踪 ID，用于排查问题", example = "aliyun-request-id")
    private String requestId;

}
