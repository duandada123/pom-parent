package com.sinogale.dwsdk;

import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.sinogale.dwsdk.config.DwSdkProperties;
import com.sinogale.dwsdk.dto.request.*;
import com.sinogale.dwsdk.dto.response.*;
import com.sinogale.dwsdk.exception.DataWorksException;
import com.sinogale.dwsdk.sdk.DataWorksGateway;
import com.sinogale.dwsdk.service.DataWorksService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import java.util.Collections;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataWorksServiceTest {
    private DataWorksGateway gateway;
    private DataWorksService service;
    @Before public void setup() {
        gateway = mock(DataWorksGateway.class);
        DwSdkProperties properties = new DwSdkProperties();
        properties.setProjectId(100L);
        service = new DataWorksService(gateway, properties);
    }
    private DeployScriptRequest request() {
        DeployScriptRequest request = new DeployScriptRequest();
        request.setFileId(10L); request.setDeploymentId(20L);
        return request;
    }
    private void deployment(int status, Integer checking, int environment, Long fileId) {
        when(gateway.deployment(any())).thenReturn(GetDeploymentPackageResponseBody.builder()
                .success(true).requestId("status-request").data(GetDeploymentPackageResponseBody.Data.builder()
                        .deployment(GetDeploymentPackageResponseBody.Deployment.builder().status(status)
                                .checkingStatus(checking).toEnvironment(environment).build())
                        .deployedItems(Collections.singletonList(GetDeploymentPackageResponseBody.DeployedItems
                                .builder().fileId(fileId).build())).build()).build());
    }
    @Test public void createMapsScriptAndSchedule() {
        CreateScriptRequest request = new CreateScriptRequest();
        request.setFileName("example"); request.setFileType(1221); request.setContent("print('ok')");
        request.setFileFolderPath("业务流程/demo"); request.setCronExpress("00 00 01 * * ?");
        when(gateway.create(any())).thenReturn(CreateFileResponseBody.builder().data(10L).requestId("create").build());
        assertEquals(Long.valueOf(10), service.create(request).getFileId());
        ArgumentCaptor<CreateFileRequest> captured = ArgumentCaptor.forClass(CreateFileRequest.class);
        verify(gateway).create(captured.capture());
        assertEquals(Long.valueOf(100), captured.getValue().getProjectId());
        assertEquals("print('ok')", captured.getValue().getContent());
        assertEquals("00 00 01 * * ?", captured.getValue().getCronExpress());
    }
    @Test public void submitPreservesDeploymentIdAndDoesNotSkipChecks() {
        SubmitScriptRequest request = new SubmitScriptRequest(); request.setFileId(10L);
        when(gateway.submit(any())).thenReturn(SubmitFileResponseBody.builder().data(20L).requestId("submit").build());
        assertEquals(Long.valueOf(20), service.submit(request).getDeploymentId());
        ArgumentCaptor<SubmitFileRequest> captured = ArgumentCaptor.forClass(SubmitFileRequest.class);
        verify(gateway).submit(captured.capture());
        assertEquals(Boolean.FALSE, captured.getValue().getSkipAllDeployFileExtensions());
    }
    @Test public void pendingDoesNotPublish() {
        deployment(0, null, 1, 10L);
        assertEquals("WAITING", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }
    @Test public void failedSubmissionDoesNotPublish() {
        deployment(2, null, 1, 10L);
        assertEquals("SUBMIT_FAILED", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }
    @Test public void pendingPackageWithoutFileItemsStillReturnsWaiting() {
        when(gateway.deployment(any())).thenReturn(GetDeploymentPackageResponseBody.builder().success(true)
                .data(GetDeploymentPackageResponseBody.Data.builder()
                        .deployment(GetDeploymentPackageResponseBody.Deployment.builder().status(0)
                                .toEnvironment(1).build()).build()).build());
        assertEquals("WAITING", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }
    @Test public void failedCheckDoesNotPublishEvenWhenStatusSuccessful() {
        deployment(1, 7, 1, 10L);
        assertEquals("CHECK_FAILED", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }
    @Test public void runningCheckDoesNotPublish() {
        deployment(1, 8, 1, 10L);
        assertEquals("WAITING", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }
    @Test public void successfulSubmissionPublishesUsingFileIdOnly() {
        deployment(1, null, 1, 10L);
        when(gateway.deploy(any())).thenReturn(DeployFileResponseBody.builder().data(30L).requestId("publish").build());
        DeployScriptResponse response = service.deploy(request());
        assertEquals("PUBLISH_ACCEPTED", response.getState());
        assertEquals(Long.valueOf(30), response.getPublishDeploymentId());
        ArgumentCaptor<DeployFileRequest> captured = ArgumentCaptor.forClass(DeployFileRequest.class);
        verify(gateway).deploy(captured.capture());
        assertEquals(Long.valueOf(10), captured.getValue().getFileId());
        assertNull(captured.getValue().getNodeId());
    }
    @Test public void wrongFileIsRejected() {
        deployment(1, null, 1, 999L);
        try { service.deploy(request()); fail(); }
        catch (DataWorksException e) { assertEquals("FILE_DEPLOYMENT_MISMATCH", e.getCode()); }
        verify(gateway, never()).deploy(any());
    }
    @Test public void productionPackageCannotAuthorizeAnotherPublish() {
        deployment(1, null, 2, 10L);
        try { service.deploy(request()); fail(); }
        catch (DataWorksException e) { assertEquals("NOT_A_SUBMISSION_PACKAGE", e.getCode()); }
        verify(gateway, never()).deploy(any());
    }
    @Test public void checkOnlyQueriesFinalPublishWithoutWriting() {
        deployment(1, null, 2, 10L);
        DeployScriptRequest request = request(); request.setCheckOnly(true); request.setFileId(null);
        DeployScriptResponse response = service.deploy(request);
        assertEquals("STATUS_ONLY", response.getState());
        assertEquals(Integer.valueOf(2), response.getToEnvironment());
        verify(gateway, never()).deploy(any());
    }
}
