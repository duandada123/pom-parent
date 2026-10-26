package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.AssertTrue;

@Data
@ApiModel(value = "DeployScriptRequest", description = "查询发布包状态或检查后发布脚本")
public class DeployScriptRequest {
    @ApiModelProperty(value = "发布包 ID；发布时传提交接口返回的 ID，仅查询时也可传生产发布包 ID", example = "2000001", required = true)
    @NotNull @Positive
    private Long deploymentId;

    @ApiModelProperty(value = "待发布文件 ID；checkOnly=false 时与 nodeId 二选一", example = "1000001")
    @Positive
    private Long fileId;

    @ApiModelProperty(value = "待发布调度节点 ID；checkOnly=false 时与 fileId 二选一", example = "3000001")
    @Positive
    private Long nodeId;

    @ApiModelProperty(value = "是否仅查询状态；true 不执行发布，默认 false", example = "false")
    private boolean checkOnly;

    @ApiModelProperty(hidden = true)
    @AssertTrue(message = "发布时 fileId 和 nodeId 必须且只能填写一个")
    public boolean isSelectorValid() {
        return checkOnly || (fileId == null) != (nodeId == null);
    }
}
