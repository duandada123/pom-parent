package com.sinogale.dksdkdemo.service;

import com.aliyun.dataworks_20240902.models.GetInstanceLogRequest;
import com.aliyun.dataworks_20240902.models.GetInstanceLogResponseBody;
import com.aliyun.dataworks_20240902.models.ListInstancesRequest;
import com.aliyun.dataworks_20240902.models.ListInstancesResponseBody;
import com.aliyun.dataworks_20240902.models.ListNodesRequest;
import com.aliyun.dataworks_20240902.models.ListNodesResponseBody;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.response.InstanceLogResponse;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class DataWorksLogService {
    private final DataWorksGateway gateway;
    private final DataWorksService files;
    private final DataWorksProperties properties;

    public DataWorksLogService(DataWorksGateway gateway, DataWorksService files,
                               DataWorksProperties properties) {
        this.gateway = gateway;
        this.files = files;
        this.properties = properties;
    }

    public QueryLogsResponse query(QueryLogsRequest request) {
        QueryLogsResponse result = new QueryLogsResponse();
        String projectEnv = normalizeProjectEnv(request.getProjectEnv());
        if (request.getInstanceId() != null) {
            InstanceLogResponse log = new InstanceLogResponse();
            log.setInstanceId(request.getInstanceId());
            log.setRunNumber(request.getRunNumber());
            attachLog(log, projectEnv);
            result.getLogs().add(log);
            result.setTotalCount(1);
            return result;
        }

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60);
        Long projectId = files.projectId(request.getProjectId());
        Long nodeId = request.getTaskId();
        if (nodeId == null) {
            ListNodesResponseBody.ListNodesResponseBodyDataNodes node =
                    resolveNode(projectId, projectEnv, request, deadline);
            nodeId = node.getNodeId();
            result.setNodeId(nodeId);
        }
        if (nodeId == null || nodeId <= 0) {
            throw new DataWorksException(409, "NODE_ID_MISSING",
                    "节点缺少有效的 nodeId，请确认节点已提交到调度系统", null);
        }
        result.setTaskId(nodeId);

        Set<String> seen = new HashSet<String>();
        for (int offset = 0; offset < request.getMaxPages(); offset++) {
            checkDeadline(deadline);
            int pageNumber = Math.addExact(request.getPageNumber(), offset);
            ListInstancesRequest sdkRequest = new ListInstancesRequest()
                    .setRegionId(properties.getRegionId())
                    .setProjectId(projectId)
                    .setProjectEnv(projectEnv)
                    .setNodeId(nodeId)
                    .setBizdate(request.getBizdate().toString())
                    .setPageNumber(pageNumber)
                    .setPageSize(request.getPageSize());
            ListInstancesResponseBody body = gateway.instances(sdkRequest);
            ListInstancesResponseBody.ListInstancesResponseBodyData page = body.getData();
            if (page.getTotalCount() == null || page.getTotalCount() < 0) {
                throw invalidPage(body.getRequestId());
            }
            List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances =
                    page.getInstances();
            if (instances == null) {
                if (page.getTotalCount() != 0) {
                    throw invalidPage(body.getRequestId());
                }
                instances = Collections.emptyList();
            }
            result.setTotalCount(page.getTotalCount());
            result.setPagesFetched(offset + 1);
            if (instances.isEmpty()
                    && (long) (pageNumber - 1) * request.getPageSize() < page.getTotalCount()) {
                throw invalidPage(body.getRequestId());
            }

            for (ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instance : instances) {
                if (instance.getInstanceId() == null) {
                    throw invalidPage(body.getRequestId());
                }
                String key = instance.getInstanceId() + ":" + instance.getTaskRerunTime();
                if (!seen.add(key)) {
                    continue;
                }
                checkDeadline(deadline);
                InstanceLogResponse log = toLog(instance);
                try {
                    attachLog(log, projectEnv);
                } catch (DataWorksException e) {
                    if (Thread.currentThread().isInterrupted() || e.getHttpStatus() == 503) {
                        throw e;
                    }
                    log.setLogSuccess(false);
                    log.setErrorCode(e.getCode());
                    log.setErrorMessage(e.getMessage());
                    log.setRequestId(e.getRequestId());
                    result.setComplete(false);
                }
                result.getLogs().add(log);
            }

            boolean hasMore = (long) pageNumber * request.getPageSize() < page.getTotalCount();
            result.setHasMore(hasMore);
            result.setNextPageNumber(hasMore ? pageNumber + 1 : null);
            if (!hasMore) {
                break;
            }
        }
        return result;
    }

    private ListNodesResponseBody.ListNodesResponseBodyDataNodes resolveNode(
            Long projectId, String projectEnv, QueryLogsRequest request, long deadline) {
        Map<Long, ListNodesResponseBody.ListNodesResponseBodyDataNodes> matches =
                new LinkedHashMap<Long, ListNodesResponseBody.ListNodesResponseBodyDataNodes>();
        for (int number = 1; number <= properties.getMaxNodePages(); number++) {
            checkDeadline(deadline);
            ListNodesRequest sdkRequest = new ListNodesRequest()
                    .setRegionId(properties.getRegionId())
                    .setProjectId(projectId)
                    .setProjectEnv(projectEnv)
                    .setNodeName(request.getFileName())
                    .setPageNumber(number)
                    .setPageSize(100);
            ListNodesResponseBody body = gateway.nodes(sdkRequest);
            ListNodesResponseBody.ListNodesResponseBodyData page = body.getData();
            Integer total = page.getTotalCount();
            if (total == null || total < 0) {
                throw invalidPage(body.getRequestId());
            }
            List<ListNodesResponseBody.ListNodesResponseBodyDataNodes> nodes = page.getNodes();
            if (nodes == null) {
                if (total != 0) {
                    throw invalidPage(body.getRequestId());
                }
                nodes = Collections.emptyList();
            }
            if (nodes.isEmpty() && (long) (number - 1) * 100 < total) {
                throw invalidPage(body.getRequestId());
            }
            for (ListNodesResponseBody.ListNodesResponseBodyDataNodes node : nodes) {
                if (request.getFileName().equals(node.getNodeName())) {
                    if (node.getNodeId() == null) {
                        throw invalidPage(body.getRequestId());
                    }
                    matches.put(node.getNodeId(), node);
                }
            }
            if (matches.size() > 1) {
                throw new DataWorksException(409, "AMBIGUOUS_NODE",
                        "存在多个同名节点，请直接提供 nodeId", body.getRequestId());
            }
            if ((long) number * 100 >= total) {
                if (matches.isEmpty()) {
                    throw new DataWorksException(404, "NODE_NOT_FOUND",
                            "未找到名称完全匹配的节点", body.getRequestId());
                }
                return matches.values().iterator().next();
            }
        }
        throw new DataWorksException(422, "NODE_SEARCH_LIMIT",
                "节点查询超过分页上限，无法确认唯一性，请直接提供 nodeId", null);
    }

    private InstanceLogResponse toLog(
            ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instance) {
        InstanceLogResponse log = new InstanceLogResponse();
        log.setInstanceId(instance.getInstanceId());
        log.setTaskId(instance.getNodeId());
        log.setTaskName(instance.getNodeName());
        log.setRunNumber(instance.getTaskRerunTime());
        log.setStatus(instance.getStatus());
        log.setBizdate(instance.getBizdate());
        log.setStartedTime(instance.getBeginRunningTime());
        log.setFinishedTime(instance.getFinishTime());
        return log;
    }

    private void attachLog(InstanceLogResponse log, String projectEnv) {
        GetInstanceLogRequest request = new GetInstanceLogRequest()
                .setInstanceId(log.getInstanceId())
                .setProjectEnv(projectEnv);
        request.setRegionId(properties.getRegionId());
        GetInstanceLogResponseBody body = gateway.log(request);
        log.setLogContent(body.getData());
        log.setLogSuccess(true);
        log.setRequestId(body.getRequestId());
    }

    private String normalizeProjectEnv(String projectEnv) {
        return projectEnv == null ? "PROD" : projectEnv.toUpperCase(Locale.ENGLISH);
    }

    private void checkDeadline(long deadline) {
        if (System.nanoTime() >= deadline) {
            throw new DataWorksException(504, "LOG_QUERY_TIME_LIMIT",
                    "本次日志聚合超过时间预算，请减少 pageSize/maxPages 或直接按 instanceId 查询", null);
        }
    }

    private DataWorksException invalidPage(String requestId) {
        return new DataWorksException(502, "INVALID_UPSTREAM_PAGE",
                "DataWorks 分页数据不完整，请重试查询", requestId);
    }
}
