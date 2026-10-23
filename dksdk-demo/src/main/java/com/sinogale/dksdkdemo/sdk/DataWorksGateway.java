package com.sinogale.dksdkdemo.sdk;

import com.aliyun.dataworks_20240902.models.*;
import com.aliyun.tea.TeaException;
import com.sinogale.dksdkdemo.config.DataWorksClientProvider;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;

/** SDK 适配层：屏蔽 20240902 SDK 的响应包装，并统一转换上游异常。 */
@Component
public class DataWorksGateway {
    private final DataWorksClientProvider provider;

    public DataWorksGateway(DataWorksClientProvider provider) {
        this.provider = provider;
    }

    public CreateFileResponseBody create(final CreateFileRequest request) {
        CreateFileResponse response = invoke("CreateFile", new SdkCall<CreateFileResponse>() {
            @Override
            public CreateFileResponse call() throws Exception {
                return provider.getClient().createFile(request);
            }
        });
        CreateFileResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }

    public SubmitFileResponseBody submit(final SubmitFileRequest request) {
        SubmitFileResponse response = invoke("SubmitFile", new SdkCall<SubmitFileResponse>() {
            @Override
            public SubmitFileResponse call() throws Exception {
                return provider.getClient().submitFile(request);
            }
        });
        SubmitFileResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }

    public DeployFileResponseBody deploy(final DeployFileRequest request) {
        DeployFileResponse response = invoke("DeployFile", new SdkCall<DeployFileResponse>() {
            @Override
            public DeployFileResponse call() throws Exception {
                return provider.getClient().deployFile(request);
            }
        });
        DeployFileResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }

    public GetDeploymentResponseBody deployment(final GetDeploymentRequest request) {
        GetDeploymentResponse response = invoke("GetDeployment", new SdkCall<GetDeploymentResponse>() {
            @Override
            public GetDeploymentResponse call() throws Exception {
                return provider.getClient().getDeployment(request);
            }
        });
        GetDeploymentResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        requireData(body.getData().getDeployment(), body.getRequestId());
        return body;
    }

    public ListNodesResponseBody nodes(final ListNodesRequest request) {
        ListNodesResponse response = invoke("ListNodes", new SdkCall<ListNodesResponse>() {
            @Override
            public ListNodesResponse call() throws Exception {
                return provider.getClient().listNodes(request);
            }
        });
        ListNodesResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }

    public ListInstancesResponseBody instances(final ListInstancesRequest request) {
        ListInstancesResponse response = invoke("ListInstances", new SdkCall<ListInstancesResponse>() {
            @Override
            public ListInstancesResponse call() throws Exception {
                return provider.getClient().listInstances(request);
            }
        });
        ListInstancesResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }

    public GetInstanceLogResponseBody log(final GetInstanceLogRequest request) {
        GetInstanceLogResponse response = invoke("GetInstanceLog", new SdkCall<GetInstanceLogResponse>() {
            @Override
            public GetInstanceLogResponse call() throws Exception {
                return provider.getClient().getInstanceLog(request);
            }
        });
        GetInstanceLogResponseBody body = response == null ? null : response.getBody();
        validate(body, body == null ? null : body.getHttpStatusCode(),
                body == null ? null : body.getSuccess(), body == null ? null : body.getErrorCode(),
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        return body;
    }

    private <T> T invoke(String action, SdkCall<T> call) {
        try {
            return call.call();
        } catch (DataWorksException e) {
            throw e;
        } catch (TeaException e) {
            String code = e.getCode() == null ? "DATAWORKS_SDK_ERROR" : e.getCode();
            throw new DataWorksException(502, code,
                    action + " 调用失败，请按 code 排查；写操作请先核对云端结果", null);
        } catch (Exception e) {
            if (hasCause(e, SocketTimeoutException.class)) {
                throw new DataWorksException(504, "DATAWORKS_TIMEOUT",
                        action + " 超时；写操作结果可能未知，请先核对云端结果再重试", null);
            }
            throw new DataWorksException(502, "DATAWORKS_SDK_ERROR",
                    action + " 调用失败；写操作请先核对云端结果", null);
        }
    }

    private void validate(Object body, Integer httpStatusCode, Boolean success, String code,
                          String errorMessage, String requestId) {
        requireData(body, requestId);
        if (httpStatusCode != null && (httpStatusCode < 200 || httpStatusCode >= 300)) {
            throw new DataWorksException(502, code == null ? "UPSTREAM_HTTP_ERROR" : code,
                    errorMessage == null ? "DataWorks HTTP 响应异常" : errorMessage, requestId);
        }
        if (!Boolean.TRUE.equals(success)) {
            throw new DataWorksException(502, code == null ? "DATAWORKS_REJECTED" : code,
                    errorMessage == null ? "DataWorks 返回业务失败" : errorMessage, requestId);
        }
    }

    private void requireData(Object value, String requestId) {
        if (value == null) {
            throw new DataWorksException(502, "INVALID_UPSTREAM_RESPONSE",
                    "DataWorks 返回缺少必要数据", requestId);
        }
    }

    private boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private interface SdkCall<T> {
        T call() throws Exception;
    }
}
