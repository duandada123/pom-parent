package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "QueryExecutionResponse", description = "按 dagId 查询到的实例状态分页")
public class QueryExecutionResponse {

    @ApiModelProperty(value = "本次执行的工作流 ID")
    private Long dagId;

    @ApiModelProperty(value = "实际查询环境")
    private String projectEnv;

    @ApiModelProperty(value = "当前页码")
    private Integer pageNumber;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "实例总数；0 可能表示实例尚未生成")
    private Integer totalCount;

    @ApiModelProperty(value = "是否还有下一页")
    private boolean hasMore;

    @ApiModelProperty(value = "DataWorks 请求追踪 ID")
    private String requestId;

    @ApiModelProperty(value = "实例状态列表；空列表属于正常结果，不代表执行成功")
    private List<ExecutionInstanceResponse> instances = new ArrayList<ExecutionInstanceResponse>();
}
