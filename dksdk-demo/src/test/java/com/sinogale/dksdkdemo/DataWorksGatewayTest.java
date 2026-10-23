package com.sinogale.dksdkdemo;

import com.aliyun.dataworks_20240902.Client;
import com.aliyun.dataworks_20240902.models.CreateFileRequest;
import com.aliyun.dataworks_20240902.models.CreateFileResponse;
import com.aliyun.dataworks_20240902.models.CreateFileResponseBody;
import com.sinogale.dksdkdemo.config.DataWorksClientProvider;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import org.junit.Before;
import org.junit.Test;

import java.net.SocketTimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataWorksGatewayTest {
    private Client client;
    private DataWorksGateway gateway;

    @Before
    public void setup() {
        client = mock(Client.class);
        DataWorksClientProvider provider = mock(DataWorksClientProvider.class);
        when(provider.getClient()).thenReturn(client);
        gateway = new DataWorksGateway(provider);
    }

    private CreateFileResponse response(boolean success) {
        CreateFileResponseBody body = new CreateFileResponseBody()
                .setSuccess(success)
                .setHttpStatusCode(200)
                .setData(10L)
                .setErrorCode("DENIED")
                .setErrorMessage("denied")
                .setRequestId("upstream-request");
        return new CreateFileResponse().setBody(body);
    }

    @Test
    public void bodyFailureIsNotMistakenForHttpSuccess() throws Exception {
        when(client.createFile(any())).thenReturn(response(false));
        try {
            gateway.create(new CreateFileRequest());
            fail();
        } catch (DataWorksException e) {
            assertEquals("DENIED", e.getCode());
            assertEquals("upstream-request", e.getRequestId());
        }
    }

    @Test
    public void typedSuccessfulResponseIsUnwrapped() throws Exception {
        when(client.createFile(any())).thenReturn(response(true));
        assertEquals(Long.valueOf(10), gateway.create(new CreateFileRequest()).getData());
    }

    @Test
    public void socketTimeoutIsMappedAndWriteIsNotRetried() throws Exception {
        when(client.createFile(any())).thenThrow(new SocketTimeoutException("test timeout"));
        try {
            gateway.create(new CreateFileRequest());
            fail();
        } catch (DataWorksException e) {
            assertEquals(504, e.getHttpStatus());
            assertEquals("DATAWORKS_TIMEOUT", e.getCode());
        }
        verify(client, times(1)).createFile(any());
    }

    @Test
    public void clientCanBeConstructedWithSdkRuntimeWithoutNetworkCall() {
        DataWorksProperties properties = new DataWorksProperties();
        properties.setAccessKeyId("test-only-id");
        properties.setAccessKeySecret("test-only-secret");
        DataWorksClientProvider provider = new DataWorksClientProvider(properties);
        assertSame(provider.getClient(), provider.getClient());
    }
}
