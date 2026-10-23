package com.sinogale.dksdkdemo;

import com.aliyun.dataworks_20240902.models.GetInstanceLogRequest;
import com.aliyun.dataworks_20240902.models.GetInstanceLogResponseBody;
import com.aliyun.dataworks_20240902.models.ListInstancesRequest;
import com.aliyun.dataworks_20240902.models.ListInstancesResponseBody;
import com.aliyun.dataworks_20240902.models.ListNodesResponseBody;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import com.sinogale.dksdkdemo.service.DataWorksLogService;
import com.sinogale.dksdkdemo.service.DataWorksService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataWorksLogServiceTest {
    private DataWorksGateway gateway;
    private DataWorksLogService service;

    @Before
    public void setup() {
        gateway = mock(DataWorksGateway.class);
        DataWorksProperties properties = new DataWorksProperties();
        properties.setProjectId(100L);
        service = new DataWorksLogService(gateway, new DataWorksService(gateway, properties), properties);
        when(gateway.log(any())).thenReturn(
                new GetInstanceLogResponseBody().setData("ok").setRequestId("log-request"));
    }

    private QueryLogsRequest request() {
        QueryLogsRequest request = new QueryLogsRequest();
        request.setTaskId(200L);
        request.setBizdate(LocalDate.of(2026, 10, 21));
        request.setPageSize(1);
        request.setMaxPages(2);
        return request;
    }

    private ListInstancesResponseBody page(int total, Long id, int rerunTime) {
        List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances =
                id == null ? Collections.<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances>emptyList()
                        : Collections.singletonList(
                                new ListInstancesResponseBody.ListInstancesResponseBodyDataInstances()
                                        .setInstanceId(id)
                                        .setNodeId(200L)
                                        .setTaskRerunTime(rerunTime)
                                        .setNodeName("demo")
                                        .setStatus("Success"));
        return new ListInstancesResponseBody()
                .setRequestId("instances-request")
                .setData(new ListInstancesResponseBody.ListInstancesResponseBodyData()
                        .setTotalCount(total)
                        .setInstances(instances));
    }

    private ListNodesResponseBody nodes(int total,
            ListNodesResponseBody.ListNodesResponseBodyDataNodes... nodes) {
        return new ListNodesResponseBody()
                .setRequestId("nodes-request")
                .setData(new ListNodesResponseBody.ListNodesResponseBodyData()
                        .setTotalCount(total)
                        .setNodes(Arrays.asList(nodes)));
    }

    @Test
    public void pagesAreReadOnceAndRerunLogsAreDistinct() {
        when(gateway.instances(any())).thenReturn(page(2, 1L, 1), page(2, 1L, 2));
        QueryLogsResponse response = service.query(request());
        assertEquals(2, response.getLogs().size());
        assertFalse(response.isHasMore());

        ArgumentCaptor<ListInstancesRequest> pages = ArgumentCaptor.forClass(ListInstancesRequest.class);
        verify(gateway, times(2)).instances(pages.capture());
        assertEquals(Integer.valueOf(1), pages.getAllValues().get(0).getPageNumber());
        assertEquals(Integer.valueOf(2), pages.getAllValues().get(1).getPageNumber());
        assertEquals("2026-10-21", pages.getValue().getBizdate());
        assertEquals(Long.valueOf(200), pages.getValue().getNodeId());
        assertEquals("PROD", pages.getValue().getProjectEnv());

        ArgumentCaptor<GetInstanceLogRequest> logs = ArgumentCaptor.forClass(GetInstanceLogRequest.class);
        verify(gateway, times(2)).log(logs.capture());
        assertEquals(Long.valueOf(1), logs.getValue().getInstanceId());
        verify(gateway, never()).nodes(any());
    }

    @Test
    public void boundedPaginationExposesContinuation() {
        when(gateway.instances(any())).thenReturn(page(3, 1L, 1));
        QueryLogsRequest request = request();
        request.setMaxPages(1);
        QueryLogsResponse result = service.query(request);
        assertTrue(result.isHasMore());
        assertEquals(Integer.valueOf(2), result.getNextPageNumber());
        verify(gateway, times(1)).instances(any());
    }

    @Test
    public void emptyInstancesDoNotFetchLog() {
        when(gateway.instances(any())).thenReturn(page(0, null, 1));
        assertTrue(service.query(request()).getLogs().isEmpty());
        verify(gateway, never()).log(any());
    }

    @Test
    public void exactNameResolvesNodeId() {
        QueryLogsRequest request = request();
        request.setTaskId(null);
        request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(nodes(2,
                new ListNodesResponseBody.ListNodesResponseBodyDataNodes()
                        .setNodeId(999L).setNodeName("demo_copy"),
                new ListNodesResponseBody.ListNodesResponseBodyDataNodes()
                        .setNodeId(200L).setNodeName("demo")));
        when(gateway.instances(any())).thenReturn(page(0, null, 1));

        QueryLogsResponse response = service.query(request);
        assertEquals(Long.valueOf(200), response.getNodeId());
        assertEquals(Long.valueOf(200), response.getTaskId());
        ArgumentCaptor<ListInstancesRequest> captured = ArgumentCaptor.forClass(ListInstancesRequest.class);
        verify(gateway).instances(captured.capture());
        assertEquals(Long.valueOf(200), captured.getValue().getNodeId());
    }

    @Test
    public void duplicateNamesAcrossPagesAreRejected() {
        QueryLogsRequest request = request();
        request.setTaskId(null);
        request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(
                nodes(101, new ListNodesResponseBody.ListNodesResponseBodyDataNodes()
                        .setNodeId(200L).setNodeName("demo")),
                nodes(101, new ListNodesResponseBody.ListNodesResponseBodyDataNodes()
                        .setNodeId(201L).setNodeName("demo")));
        try {
            service.query(request);
            fail();
        } catch (DataWorksException e) {
            assertEquals("AMBIGUOUS_NODE", e.getCode());
        }
        verify(gateway, times(2)).nodes(any());
        verify(gateway, never()).instances(any());
    }

    @Test
    public void noExactNameIsNotFound() {
        QueryLogsRequest request = request();
        request.setTaskId(null);
        request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(nodes(0));
        try {
            service.query(request);
            fail();
        } catch (DataWorksException e) {
            assertEquals("NODE_NOT_FOUND", e.getCode());
        }
    }

    @Test
    public void individualFailureIsExplicitAndOtherLogsSurvive() {
        when(gateway.instances(any())).thenReturn(page(2, 1L, 1), page(2, 2L, 1));
        when(gateway.log(any()))
                .thenThrow(new DataWorksException(502, "LOG_FAILED", "test", "failed-request"))
                .thenReturn(new GetInstanceLogResponseBody().setData("ok"));
        QueryLogsResponse response = service.query(request());
        assertFalse(response.isComplete());
        assertEquals(2, response.getLogs().size());
        assertEquals("LOG_FAILED", response.getLogs().get(0).getErrorCode());
        assertTrue(response.getLogs().get(1).isLogSuccess());
    }

    @Test
    public void directInstanceSkipsNodeAndInstanceListCalls() {
        QueryLogsRequest request = new QueryLogsRequest();
        request.setInstanceId(99L);
        assertEquals("ok", service.query(request).getLogs().get(0).getLogContent());
        verify(gateway, never()).nodes(any());
        verify(gateway, never()).instances(any());
    }
}
