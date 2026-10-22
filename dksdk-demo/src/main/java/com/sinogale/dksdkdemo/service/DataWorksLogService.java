package com.sinogale.dksdkdemo.service;

import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DataWorksLogService {
    private final DataWorksGateway gateway;
    private final DataWorksService files;
    private final DataWorksProperties properties;
    public DataWorksLogService(DataWorksGateway gateway, DataWorksService files, DataWorksProperties properties) {
        this.gateway = gateway; this.files = files; this.properties = properties;
    }

    public QueryLogsResponse query(QueryLogsRequest request) {
        QueryLogsResponse result = new QueryLogsResponse();
        if (request.getInstanceId() != null) {
            InstanceLogResponse log = new InstanceLogResponse();
            log.setInstanceId(request.getInstanceId()); log.setRunNumber(request.getRunNumber());
            attachLog(log); // 单实例失败直接返回 HTTP 错误。
            result.getLogs().add(log); result.setTotalCount(1);
            return result;
        }
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
        Long projectId = files.projectId(request.getProjectId());
        Long taskId = request.getTaskId();
        if (taskId == null) {
            ListNodesResponseBody.Nodes node = resolveNode(projectId, request, deadline);
            result.setNodeId(node.getId());
            taskId = node.getTaskId();
            if (taskId == null || taskId <= 0) throw new DataWorksException(409, "NODE_HAS_NO_TASK",
                    "节点尚无调度 taskId，请先完成提交/发布", null);
        }
        result.setTaskId(taskId);
        long bizdate = request.getBizdate().atStartOfDay(ZoneId.of(properties.getBizdateZone()))
                .toInstant().toEpochMilli();
        String env = "Dev".equalsIgnoreCase(request.getProjectEnv()) ? "Dev" : "Prod";
        Set<String> seen = new HashSet<>();
        for (int offset = 0; offset < request.getMaxPages(); offset++) {
            checkDeadline(deadline);
            int pageNumber = Math.addExact(request.getPageNumber(), offset);
            ListTaskInstancesResponseBody body = gateway.instances(ListTaskInstancesRequest.builder()
                    .regionId(properties.getRegionId()).projectId(projectId).projectEnv(env)
                    .taskId(taskId).bizdate(bizdate).sortBy("Id Desc")
                    .pageNumber(pageNumber).pageSize(request.getPageSize()).build());
            ListTaskInstancesResponseBody.PagingInfo page = body.getPagingInfo();
            if (page.getTotalCount() == null || page.getTotalCount() < 0) throw invalidPage(body.getRequestId());
            List<ListTaskInstancesResponseBody.TaskInstances> instances = page.getTaskInstances();
            if (instances == null) {
                if (page.getTotalCount() != 0) throw invalidPage(body.getRequestId());
                instances = Collections.emptyList();
            }
            result.setTotalCount(page.getTotalCount());
            result.setPagesFetched(offset + 1);
            if (instances.isEmpty() && (long)(pageNumber - 1) * request.getPageSize() < page.getTotalCount()) {
                throw invalidPage(body.getRequestId());
            }
            for (ListTaskInstancesResponseBody.TaskInstances instance : instances) {
                if (instance.getId() == null) throw invalidPage(body.getRequestId());
                // 同一实例的不同重跑次数必须保留，不能只按 instanceId 去重。
                String key = instance.getId() + ":" + instance.getRunNumber();
                if (!seen.add(key)) continue;
                checkDeadline(deadline);
                InstanceLogResponse log = toLog(instance);
                try {
                    attachLog(log);
                } catch (DataWorksException e) {
                    if (Thread.currentThread().isInterrupted() || e.getHttpStatus() == 503) throw e;
                    log.setLogSuccess(false); log.setErrorCode(e.getCode());
                    log.setErrorMessage(e.getMessage()); log.setRequestId(e.getRequestId());
                    result.setComplete(false);
                }
                result.getLogs().add(log);
            }
            boolean hasMore = (long) pageNumber * request.getPageSize() < page.getTotalCount();
            result.setHasMore(hasMore);
            result.setNextPageNumber(hasMore ? pageNumber + 1 : null);
            if (!hasMore) break;
        }
        return result;
    }

    private ListNodesResponseBody.Nodes resolveNode(Long projectId, QueryLogsRequest request, long deadline) {
        Map<String, ListNodesResponseBody.Nodes> matches = new LinkedHashMap<>();
        for (int number = 1; number <= properties.getMaxNodePages(); number++) {
            checkDeadline(deadline);
            ListNodesResponseBody body = gateway.nodes(ListNodesRequest.builder()
                    .regionId(properties.getRegionId()).projectId(projectId).name(request.getFileName())
                    .containerId(request.getContainerId()).scene(request.getScene())
                    .pageNumber(number).pageSize(100).build());
            ListNodesResponseBody.PagingInfo page = body.getPagingInfo();
            long total;
            try { total = Long.parseLong(page.getTotalCount()); }
            catch (RuntimeException e) { throw invalidPage(body.getRequestId()); }
            if (total < 0) throw invalidPage(body.getRequestId());
            List<ListNodesResponseBody.Nodes> nodes = page.getNodes();
            if (nodes == null) {
                if (total != 0) throw invalidPage(body.getRequestId());
                nodes = Collections.emptyList();
            }
            if (nodes.isEmpty() && (long)(number - 1) * 100 < total) throw invalidPage(body.getRequestId());
            for (ListNodesResponseBody.Nodes node : nodes) {
                if (request.getFileName().equals(node.getName())) {
                    if (node.getId() == null) throw invalidPage(body.getRequestId());
                    matches.put(node.getId(), node);
                }
            }
            if (matches.size() > 1) throw new DataWorksException(409, "AMBIGUOUS_NODE",
                    "存在多个同名节点，请提供 taskId，或通过 containerId/scene 缩小范围", body.getRequestId());
            if ((long) number * 100 >= total) {
                if (matches.isEmpty()) throw new DataWorksException(404, "NODE_NOT_FOUND",
                        "未找到名称完全匹配的节点", body.getRequestId());
                return matches.values().iterator().next();
            }
        }
        throw new DataWorksException(422, "NODE_SEARCH_LIMIT",
                "节点查询超过分页上限，无法确认唯一性，请提供 taskId 或缩小查询范围", null);
    }

    private InstanceLogResponse toLog(ListTaskInstancesResponseBody.TaskInstances instance) {
        InstanceLogResponse log = new InstanceLogResponse();
        log.setInstanceId(instance.getId()); log.setTaskId(instance.getTaskId());
        log.setTaskName(instance.getTaskName()); log.setRunNumber(instance.getRunNumber());
        log.setStatus(instance.getStatus()); log.setBizdate(instance.getBizdate());
        log.setStartedTime(instance.getStartedTime()); log.setFinishedTime(instance.getFinishedTime());
        return log;
    }
    private void attachLog(InstanceLogResponse log) {
        GetTaskInstanceLogResponseBody body = gateway.log(GetTaskInstanceLogRequest.builder()
                .regionId(properties.getRegionId()).id(log.getInstanceId()).runNumber(log.getRunNumber()).build());
        // null/空日志可以表示任务尚未产生内容，不伪造为 SDK 错误。
        log.setLogContent(body.getTaskInstanceLog()); log.setLogSuccess(true);
        log.setRequestId(body.getRequestId());
    }
    private void checkDeadline(long deadline) {
        if (System.nanoTime() >= deadline) throw new DataWorksException(504, "LOG_QUERY_TIME_LIMIT",
                "本次日志聚合超过时间预算，请减少 pageSize/maxPages 或直接按 instanceId 查询", null);
    }
    private DataWorksException invalidPage(String requestId) {
        return new DataWorksException(502, "INVALID_UPSTREAM_PAGE", "DataWorks 分页数据不完整，请重试查询", requestId);
    }
}
