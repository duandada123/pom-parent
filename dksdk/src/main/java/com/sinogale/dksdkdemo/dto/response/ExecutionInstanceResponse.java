package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ExecutionInstanceResponse", description = "DataWorks 执行实例状态")
public class ExecutionInstanceResponse {

    @ApiModelProperty(value = "实例 ID，可用于查询运行日志", example = "5000001")
    private Long instanceId;

    @ApiModelProperty(value = "实例所属节点 ID", example = "3000001")
    private Long nodeId;

    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    @ApiModelProperty(value = "DataWorks 返回的实例原始状态")
    private String status;

    @ApiModelProperty(value = "业务日期，毫秒时间戳")
    private Long bizdate;

    @ApiModelProperty(value = "开始运行时间，毫秒时间戳；未开始时可能为空")
    private Long beginRunningTime;

    @ApiModelProperty(value = "完成时间，毫秒时间戳；未完成时可能为空")
    private Long finishTime;

    @ApiModelProperty(value = "实例错误信息；未失败时可能为空")
    private String errorMessage;
}
