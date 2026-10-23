package com.sinogale.dksdkdemo;

import com.aliyun.dataworks_20240902.models.CreateFileRequest;
import com.aliyun.dataworks_20240902.models.CreateFileResponseBody;
import com.aliyun.dataworks_20240902.models.DeployFileRequest;
import com.aliyun.dataworks_20240902.models.DeployFileResponseBody;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponseBody;
import com.aliyun.dataworks_20240902.models.SubmitFileRequest;
import com.aliyun.dataworks_20240902.models.SubmitFileResponseBody;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import com.sinogale.dksdkdemo.service.DataWorksService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataWorksServiceTest {
    private DataWorksGateway gateway;
    private DataWorksService service;

    @Before
    public void setup() {
        gateway = mock(DataWorksGateway.class);
        DataWorksProperties properties = new DataWorksProperties();
        properties.setProjectId(100L);
        service = new DataWorksService(gateway, properties);
    }

    private DeployScriptRequest request() {
        DeployScriptRequest request = new DeployScriptRequest();
        request.setFileId(10L);
        request.setDeploymentId(20L);
        return request;
    }

    private void deployment(int status, Integer checking, int environment) {
        GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                new GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment()
                        .setStatus(status)
                        .setCheckingStatus(checking)
                        .setToEnvironment(environment);
        GetDeploymentResponseBody body = new GetDeploymentResponseBody()
                .setSuccess(true)
                .setRequestId("status-request")
                .setData(new GetDeploymentResponseBody.GetDeploymentResponseBodyData()
                        .setDeployment(deployment));
        when(gateway.deployment(any())).thenReturn(body);
    }

    @Test
    public void createMapsScriptAndSchedule() {
        CreateScriptRequest request = new CreateScriptRequest();
        request.setFileName("example");
        request.setFileType(1221);
        request.setContent("print('ok')");
        request.setFileFolderPath("业务流程/demo");
        request.setCronExpress("00 00 01 * * ?");
        when(gateway.create(any())).thenReturn(
                new CreateFileResponseBody().setData(10L).setRequestId("create"));

        assertEquals(Long.valueOf(10), service.create(request).getFileId());
        ArgumentCaptor<CreateFileRequest> captured = ArgumentCaptor.forClass(CreateFileRequest.class);
        verify(gateway).create(captured.capture());
        assertEquals(Long.valueOf(100), captured.getValue().getProjectId());
        assertEquals("print('ok')", captured.getValue().getContent());
        assertEquals("00 00 01 * * ?", captured.getValue().getCronExpress());
    }

    @Test
    public void submitPreservesDeploymentId() {
        SubmitScriptRequest request = new SubmitScriptRequest();
        request.setFileId(10L);
        when(gateway.submit(any())).thenReturn(
                new SubmitFileResponseBody().setData(20L).setRequestId("submit"));

        assertEquals(Long.valueOf(20), service.submit(request).getDeploymentId());
        ArgumentCaptor<SubmitFileRequest> captured = ArgumentCaptor.forClass(SubmitFileRequest.class);
        verify(gateway).submit(captured.capture());
        assertEquals(Long.valueOf(10), captured.getValue().getFileId());
        assertEquals(Long.valueOf(100), captured.getValue().getProjectId());
    }

    @Test
    public void pendingDoesNotPublish() {
        deployment(0, null, 1);
        assertEquals("WAITING", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }

    @Test
    public void failedSubmissionDoesNotPublish() {
        deployment(2, null, 1);
        assertEquals("SUBMIT_FAILED", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }

    @Test
    public void failedCheckDoesNotPublishEvenWhenStatusSuccessful() {
        deployment(1, 7, 1);
        assertEquals("CHECK_FAILED", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }

    @Test
    public void runningCheckDoesNotPublish() {
        deployment(1, 8, 1);
        assertEquals("WAITING", service.deploy(request()).getState());
        verify(gateway, never()).deploy(any());
    }

    @Test
    public void successfulSubmissionPublishesUsingFileIdOnly() {
        deployment(1, null, 1);
        when(gateway.deploy(any())).thenReturn(
                new DeployFileResponseBody().setData(30L).setRequestId("publish"));

        DeployScriptResponse response = service.deploy(request());
        assertEquals("PUBLISH_ACCEPTED", response.getState());
        assertEquals(Long.valueOf(30), response.getPublishDeploymentId());
        ArgumentCaptor<DeployFileRequest> captured = ArgumentCaptor.forClass(DeployFileRequest.class);
        verify(gateway).deploy(captured.capture());
        assertEquals(Long.valueOf(10), captured.getValue().getFileId());
        assertNull(captured.getValue().getNodeId());
    }

    @Test
    public void productionPackageCannotAuthorizeAnotherPublish() {
        deployment(1, null, 2);
        try {
            service.deploy(request());
            fail();
        } catch (DataWorksException e) {
            assertEquals("NOT_A_SUBMISSION_PACKAGE", e.getCode());
        }
        verify(gateway, never()).deploy(any());
    }

    @Test
    public void checkOnlyQueriesFinalPublishWithoutWriting() {
        deployment(1, null, 2);
        DeployScriptRequest request = request();
        request.setCheckOnly(true);
        request.setFileId(null);
        DeployScriptResponse response = service.deploy(request);
        assertEquals("STATUS_ONLY", response.getState());
        assertEquals(Integer.valueOf(2), response.getToEnvironment());
        verify(gateway, never()).deploy(any());
    }
}
