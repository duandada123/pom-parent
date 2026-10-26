package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.sinogale.dksdkdemo.enums.DataWorksOperationState;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "RunScriptResponse", description = "补数据工作流创建请求受理结果，不表示任务执行完成")
public class RunScriptResponse {
    @ApiModelProperty(value = "RUN_ACCEPTED 表示运行请求已受理", example = "RUN_ACCEPTED")
    private DataWorksOperationState state;

    @ApiModelProperty(value = "补数据工作流 ID 列表；每个 dagId 可用于查询实例状态，不是 instanceId")
    private List<Long> dagIds = new ArrayList<Long>();

    @ApiModelProperty(value = "本次补数据的起始节点 ID", example = "3000001")
    private Long rootNodeId;

    @ApiModelProperty(value = "本次实际运行环境", example = "PROD")
    private String projectEnv;

    @ApiModelProperty(value = "DataWorks 请求追踪 ID")
    private String requestId;

}
