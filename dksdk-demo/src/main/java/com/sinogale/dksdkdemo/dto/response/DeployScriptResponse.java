package com.sinogale.dksdkdemo.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "DeployScriptResponse", description = "发布及状态查询结果")
@Getter
@Setter
public class DeployScriptResponse {

    @ApiModelProperty(value = "DataWorks 文件 ID", example = "1000001")
    private Long fileId;

    @ApiModelProperty(value = "发布包 ID", example = "3000001")
    private Long deploymentId;

    @ApiModelProperty(value = "本次发布返回的新包 ID；用于 checkOnly=true 查询最终发布结果", example = "4000001")
    private Long publishDeploymentId;

    @ApiModelProperty(value = "当前查询发布包状态：0 就绪，1 成功，2 失败；不是新发布包的最终状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "发布检查状态：7 未通过，8 检查中，null 才能继续发布", example = "8")
    private Integer checkingStatus;

    @ApiModelProperty(value = "发布包目标环境：1 开发，2 生产", example = "1")
    private Integer toEnvironment;

    @ApiModelProperty(value = "业务处理状态", example = "WAITING", allowableValues = "STATUS_ONLY,WAITING,SUBMIT_FAILED,CHECK_FAILED,PUBLISH_ACCEPTED")
    private String state;

    @ApiModelProperty(value = "处理结果说明")
    private String message;

    @ApiModelProperty(value = "阿里云请求追踪 ID，用于排查问题", example = "aliyun-request-id")
    private String requestId;

}
