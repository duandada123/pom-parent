package com.sinogale.dksdkdemo.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Positive;
import javax.validation.constraints.AssertTrue;

@Data
@ApiModel(value = "QueryLogsRequest", description = "查询单实例日志或节点昨日实例日志")
public class QueryLogsRequest {
    @ApiModelProperty(value = "实例 ID；与 nodeId 必须且只能填写一个", example = "9000001")
    @Positive
    private Long instanceId;

    @ApiModelProperty(value = "调度节点 ID；查询该节点昨日实例日志，与 instanceId 必须且只能填写一个", example = "3000001")
    @Positive
    private Long nodeId;

    @ApiModelProperty(hidden = true)
    @AssertTrue(message = "instanceId 和 nodeId 必须且只能填写一个")
    public boolean isSelectorValid() {
        return (instanceId == null) != (nodeId == null);
    }
}
