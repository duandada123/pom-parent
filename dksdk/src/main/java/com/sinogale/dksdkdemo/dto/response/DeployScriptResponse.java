package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.sinogale.dksdkdemo.enums.DataWorksOperationState;

@Data
@ApiModel(value = "DeployScriptResponse", description = "发布包状态及脚本发布结果")
public class DeployScriptResponse {
    @ApiModelProperty(value = "处理状态：STATUS_ONLY 仅查询；WAITING 等待完成；SUBMIT_FAILED 提交失败；CHECK_FAILED 检查失败；PUBLISH_ACCEPTED 发布已受理", example = "STATUS_ONLY")
    private DataWorksOperationState state;

    @ApiModelProperty(value = "本次查询的发布包 ID，对应请求中的 deploymentId", example = "2000001")
    private Long deploymentId;

    @ApiModelProperty(value = "本次发布生成的新发布包 ID；仅发布受理后返回，需继续查询最终状态", example = "2000002")
    private Long publishDeploymentId;

    @ApiModelProperty(value = "所查询发布包的状态；当前 Service 按 0 等待、1 成功、其他值失败处理", example = "1")
    private Integer status;

    @ApiModelProperty(value = "上游扩展检查状态；当前 Service 按 null 无待处理检查、7 检查失败、其他非空值等待处理")
    private Integer checkingStatus;

    @ApiModelProperty(value = "发布包来源环境编码，按上游原值返回")
    private Integer fromEnvironment;

    @ApiModelProperty(value = "发布包目标环境编码；当前 Service 要求发布前的提交包目标为开发环境 1", example = "1")
    private Integer toEnvironment;

    @ApiModelProperty(value = "发布包返回的错误信息，成功时可能为空")
    private String errorMessage;

    @ApiModelProperty(value = "查询发布包状态接口的请求追踪 ID")
    private String statusRequestId;

    @ApiModelProperty(value = "执行发布接口的请求追踪 ID；未执行发布时为空")
    private String publishRequestId;

}
