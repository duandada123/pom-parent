package com.sinogale.dwsdk;

import com.aliyun.sdk.service.dataworks_public20240518.AsyncClient;
import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.aliyun.sdk.gateway.pop.exception.PopClientException;
import com.sinogale.dwsdk.config.*;
import com.sinogale.dwsdk.exception.DataWorksException;
import com.sinogale.dwsdk.sdk.DataWorksGateway;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.CompletableFuture;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DataWorksGatewayTest {
    private AsyncClient client;
    private DataWorksGateway gateway;
    @Before public void setup() {
        client = mock(AsyncClient.class);
        DataWorksClientProvider provider = mock(DataWorksClientProvider.class);
        when(provider.getClient()).thenReturn(client);
        DwSdkProperties properties = new DwSdkProperties(); properties.setCallTimeoutSeconds(1);
        gateway = new DataWorksGateway(provider, properties);
    }
    private CreateFileResponse response(boolean success) {
        return CreateFileResponse.create().toBuilder().statusCode(200).body(
                CreateFileResponseBody.builder().success(success).data(10L)
                        .errorCode("DENIED").requestId("upstream-request").build()).build();
    }
    @Test public void bodyFailureIsNotMistakenForHttpSuccess() {
        when(client.createFile(any())).thenReturn(CompletableFuture.completedFuture(response(false)));
        try { gateway.create(CreateFileRequest.builder().build()); fail(); }
        catch (DataWorksException e) {
            assertEquals("DENIED", e.getCode()); assertEquals("upstream-request", e.getRequestId());
        }
    }
    @Test public void typedSuccessfulResponseIsUnwrapped() {
        when(client.createFile(any())).thenReturn(CompletableFuture.completedFuture(response(true)));
        assertEquals(Long.valueOf(10), gateway.create(CreateFileRequest.builder().build()).getData());
    }
    @Test public void failedFuturePreservesUpstreamCodeAndRequestId() {
        PopClientException failure = new PopClientException();
        failure.setErrCode("Forbidden"); failure.setRequestId("trace-1");
        CompletableFuture<CreateFileResponse> future = new CompletableFuture<>();
        future.completeExceptionally(failure);
        when(client.createFile(any())).thenReturn(future);
        try { gateway.create(CreateFileRequest.builder().build()); fail(); }
        catch (DataWorksException e) {
            assertEquals("Forbidden", e.getCode()); assertEquals("trace-1", e.getRequestId());
        }
    }
    @Test public void timeoutCancelsWaitAndNeverRetriesWrite() {
        CompletableFuture<CreateFileResponse> future = new CompletableFuture<>();
        when(client.createFile(any())).thenReturn(future);
        try { gateway.create(CreateFileRequest.builder().build()); fail(); }
        catch (DataWorksException e) { assertEquals(504, e.getHttpStatus()); }
        assertTrue(future.isCancelled()); verify(client, times(1)).createFile(any());
    }
    @Test public void interruptionRestoresInterruptFlag() {
        when(client.createFile(any())).thenReturn(new CompletableFuture<>());
        Thread.currentThread().interrupt();
        try { gateway.create(CreateFileRequest.builder().build()); fail(); }
        catch (DataWorksException e) {
            assertEquals("REQUEST_INTERRUPTED", e.getCode());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally { Thread.interrupted(); }
    }
    @Test public void clientCanBeConstructedWithSdkRuntimeWithoutNetworkCall() {
        DwSdkProperties properties = new DwSdkProperties();
        properties.setAccessKeyId("test-only-id"); properties.setAccessKeySecret("test-only-secret");
        DataWorksClientProvider provider = new DataWorksClientProvider(properties);
        try { assertSame(provider.getClient(), provider.getClient()); }
        finally { provider.close(); }
    }
}
