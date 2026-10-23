package com.sinogale.dksdkdemo.dto.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "QueryLogsResponse", description = "日志查询结果")
@Getter
@Setter
public class QueryLogsResponse {

    @ApiModelProperty(value = "调度节点 ID（兼容字段名 taskId）", example = "2000001")
    private Long taskId;

    @ApiModelProperty(value = "调度节点 ID；按文件名查询时返回", example = "2000001")
    private Long nodeId;

    @ApiModelProperty(value = "符合条件的实例总数", example = "30")
    private Integer totalCount;

    @ApiModelProperty(value = "本次实际读取的实例页数；直接查实例时为 0", example = "1")
    private int pagesFetched;

    @ApiModelProperty(value = "续读起始页码；没有更多数据时为 null", example = "2")
    private Integer nextPageNumber;

    @ApiModelProperty(value = "是否仍有未读取的实例页", example = "true")
    private boolean hasMore;

    @ApiModelProperty(value = "本次返回的日志是否全部读取成功；不表示已读完所有页", example = "true")
    private boolean complete = true;

    @ApiModelProperty(value = "本次返回的实例日志列表")
    private List<InstanceLogResponse> logs = new ArrayList<>();

}
