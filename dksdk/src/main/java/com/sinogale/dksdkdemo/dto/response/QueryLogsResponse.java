package com.sinogale.dksdkdemo.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.sinogale.dksdkdemo.entity.DwInstanceLogDetail;
import java.util.Collections;
import java.util.List;

@Data
@ApiModel(value = "QueryLogsResponse", description = "运行日志查询结果")
public class QueryLogsResponse {
    @ApiModelProperty(value = "按实例查询时返回的实例 ID", example = "9000001")
    private Long instanceId;

    @ApiModelProperty(value = "按节点查询时返回的调度节点 ID", example = "3000001")
    private Long nodeId;

    @ApiModelProperty(value = "按 instanceId 查询时的日志内容；日志尚未产生时可能为空")
    private String logContent;

    @ApiModelProperty(value = "按 nodeId 查询时的昨日实例日志明细；默认空列表")
    private List<DwInstanceLogDetail> logs = Collections.emptyList();

}
