package com.sinogale.dwsdk;

import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.sinogale.dwsdk.config.DwSdkProperties;
import com.sinogale.dwsdk.dto.request.QueryLogsRequest;
import com.sinogale.dwsdk.dto.response.QueryLogsResponse;
import com.sinogale.dwsdk.exception.DataWorksException;
import com.sinogale.dwsdk.sdk.DataWorksGateway;
import com.sinogale.dwsdk.service.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import java.time.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataWorksLogServiceTest {
    private DataWorksGateway gateway;
    private DataWorksLogService service;
    @Before public void setup() {
        gateway = mock(DataWorksGateway.class);
        DwSdkProperties properties = new DwSdkProperties(); properties.setProjectId(100L);
        service = new DataWorksLogService(gateway, new DataWorksService(gateway, properties), properties);
        when(gateway.log(any())).thenReturn(GetTaskInstanceLogResponseBody.builder().taskInstanceLog("ok")
                .requestId("log-request").build());
    }
    private QueryLogsRequest request() {
        QueryLogsRequest request = new QueryLogsRequest();
        request.setTaskId(200L); request.setBizdate(LocalDate.of(2026, 10, 21));
        request.setPageSize(1); request.setMaxPages(2);
        return request;
    }
    private ListTaskInstancesResponseBody page(int total, Long id, int run) {
        List<ListTaskInstancesResponseBody.TaskInstances> instances = id == null ? Collections.emptyList()
                : Collections.singletonList(ListTaskInstancesResponseBody.TaskInstances.builder()
                        .id(id).taskId(200L).runNumber(run).taskName("demo").status("Success").build());
        return ListTaskInstancesResponseBody.builder().pagingInfo(
                ListTaskInstancesResponseBody.PagingInfo.builder().totalCount(total).taskInstances(instances).build()).build();
    }
    private ListNodesResponseBody nodes(String total, ListNodesResponseBody.Nodes... nodes) {
        return ListNodesResponseBody.builder().pagingInfo(ListNodesResponseBody.PagingInfo.builder()
                .totalCount(total).nodes(Arrays.asList(nodes)).build()).build();
    }
    @Test public void pagesAreReadOnceAndRerunLogsAreDistinct() {
        when(gateway.instances(any())).thenReturn(page(2, 1L, 1), page(2, 1L, 2));
        QueryLogsResponse response = service.query(request());
        assertEquals(2, response.getLogs().size()); assertFalse(response.isHasMore());
        ArgumentCaptor<ListTaskInstancesRequest> pages = ArgumentCaptor.forClass(ListTaskInstancesRequest.class);
        verify(gateway, times(2)).instances(pages.capture());
        assertEquals(Integer.valueOf(1), pages.getAllValues().get(0).getPageNumber());
        assertEquals(Integer.valueOf(2), pages.getAllValues().get(1).getPageNumber());
        assertEquals(Long.valueOf(LocalDate.of(2026,10,21).atStartOfDay(ZoneId.of("Asia/Shanghai"))
                .toInstant().toEpochMilli()), pages.getValue().getBizdate());
        ArgumentCaptor<GetTaskInstanceLogRequest> logs = ArgumentCaptor.forClass(GetTaskInstanceLogRequest.class);
        verify(gateway, times(2)).log(logs.capture());
        assertEquals(Integer.valueOf(2), logs.getValue().getRunNumber());
        verify(gateway, never()).nodes(any());
    }
    @Test public void boundedPaginationExposesContinuation() {
        when(gateway.instances(any())).thenReturn(page(3, 1L, 1));
        QueryLogsRequest request = request(); request.setMaxPages(1);
        QueryLogsResponse result = service.query(request);
        assertTrue(result.isHasMore()); assertEquals(Integer.valueOf(2), result.getNextPageNumber());
        verify(gateway, times(1)).instances(any());
    }
    @Test public void emptyInstancesDoNotFetchLog() {
        when(gateway.instances(any())).thenReturn(page(0, null, 1));
        assertTrue(service.query(request()).getLogs().isEmpty());
        verify(gateway, never()).log(any());
    }
    @Test public void exactNameResolvesTaskIdRatherThanAuthoringNodeId() {
        QueryLogsRequest request = request(); request.setTaskId(null); request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(nodes("2",
                ListNodesResponseBody.Nodes.builder().id("wrong").name("demo_copy").taskId(999L).build(),
                ListNodesResponseBody.Nodes.builder().id("authoring-123").name("demo").taskId(200L).build()));
        when(gateway.instances(any())).thenReturn(page(0, null, 1));
        QueryLogsResponse response = service.query(request);
        assertEquals("authoring-123", response.getNodeId()); assertEquals(Long.valueOf(200), response.getTaskId());
        ArgumentCaptor<ListTaskInstancesRequest> captured = ArgumentCaptor.forClass(ListTaskInstancesRequest.class);
        verify(gateway).instances(captured.capture());
        assertEquals(Long.valueOf(200), captured.getValue().getTaskId());
    }
    @Test public void duplicateNamesAcrossPagesAreRejected() {
        QueryLogsRequest request = request(); request.setTaskId(null); request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(
                nodes("101", ListNodesResponseBody.Nodes.builder().id("a").name("demo").taskId(200L).build()),
                nodes("101", ListNodesResponseBody.Nodes.builder().id("b").name("demo").taskId(201L).build()));
        try { service.query(request); fail(); }
        catch (DataWorksException e) { assertEquals("AMBIGUOUS_NODE", e.getCode()); }
        verify(gateway, times(2)).nodes(any());
        verify(gateway, never()).instances(any());
    }
    @Test public void noExactNameIsNotFound() {
        QueryLogsRequest request = request(); request.setTaskId(null); request.setFileName("demo");
        when(gateway.nodes(any())).thenReturn(nodes("0"));
        try { service.query(request); fail(); }
        catch (DataWorksException e) { assertEquals("NODE_NOT_FOUND", e.getCode()); }
    }
    @Test public void individualFailureIsExplicitAndOtherLogsSurvive() {
        when(gateway.instances(any())).thenReturn(page(2, 1L, 1), page(2, 2L, 1));
        when(gateway.log(any())).thenThrow(new DataWorksException(502, "LOG_FAILED", "test", "failed-request"))
                .thenReturn(GetTaskInstanceLogResponseBody.builder().taskInstanceLog("ok").build());
        QueryLogsResponse response = service.query(request());
        assertFalse(response.isComplete()); assertEquals(2, response.getLogs().size());
        assertEquals("LOG_FAILED", response.getLogs().get(0).getErrorCode());
        assertTrue(response.getLogs().get(1).isLogSuccess());
    }
    @Test public void directInstanceSkipsNodeAndInstanceListCalls() {
        QueryLogsRequest request = new QueryLogsRequest(); request.setInstanceId(99L);
        assertEquals("ok", service.query(request).getLogs().get(0).getLogContent());
        verify(gateway, never()).nodes(any()); verify(gateway, never()).instances(any());
    }
}
