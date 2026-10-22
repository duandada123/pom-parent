package com.sinogale.dksdkdemo.sdk;

import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.aliyun.sdk.gateway.pop.exception.PopClientException;
import com.aliyun.sdk.gateway.pop.exception.PopServerException;
import com.aliyun.sdk.gateway.pop.models.Response;
import com.sinogale.dksdkdemo.config.DataWorksClientProvider;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

/** SDK 适配层：所有调用强类型、统一超时及上游错误处理。 */
@Component
public class DataWorksGateway {
    private final DataWorksClientProvider provider;
    private final DataWorksProperties properties;
    public DataWorksGateway(DataWorksClientProvider provider, DataWorksProperties properties) {
        this.provider = provider; this.properties = properties;
    }

    public CreateFileResponseBody create(CreateFileRequest request) {
        CreateFileResponseBody body = invoke("CreateFile",
                () -> provider.getClient().createFile(request), CreateFileResponse::getStatusCode, CreateFileResponse::getBody);
        requireSuccess(body.getSuccess(), body.getErrorCode(), body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }
    public SubmitFileResponseBody submit(SubmitFileRequest request) {
        SubmitFileResponseBody body = invoke("SubmitFile",
                () -> provider.getClient().submitFile(request), SubmitFileResponse::getStatusCode, SubmitFileResponse::getBody);
        requireSuccess(body.getSuccess(), body.getErrorCode(), body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }
    public DeployFileResponseBody deploy(DeployFileRequest request) {
        DeployFileResponseBody body = invoke("DeployFile",
                () -> provider.getClient().deployFile(request), DeployFileResponse::getStatusCode, DeployFileResponse::getBody);
        requireSuccess(body.getSuccess(), body.getErrorCode(), body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        return body;
    }
    public GetDeploymentPackageResponseBody deployment(GetDeploymentPackageRequest request) {
        GetDeploymentPackageResponseBody body = invoke("GetDeploymentPackage",
                () -> provider.getClient().getDeploymentPackage(request), GetDeploymentPackageResponse::getStatusCode, GetDeploymentPackageResponse::getBody);
        requireSuccess(body.getSuccess(), body.getErrorCode(), body.getRequestId());
        requireData(body.getData(), body.getRequestId());
        requireData(body.getData().getDeployment(), body.getRequestId());
        return body;
    }
    public ListNodesResponseBody nodes(ListNodesRequest request) {
        ListNodesResponseBody body = invoke("ListNodes",
                () -> provider.getClient().listNodes(request), ListNodesResponse::getStatusCode, ListNodesResponse::getBody);
        requireData(body.getPagingInfo(), body.getRequestId());
        return body;
    }
    public ListTaskInstancesResponseBody instances(ListTaskInstancesRequest request) {
        ListTaskInstancesResponseBody body = invoke("ListTaskInstances",
                () -> provider.getClient().listTaskInstances(request), ListTaskInstancesResponse::getStatusCode, ListTaskInstancesResponse::getBody);
        requireData(body.getPagingInfo(), body.getRequestId());
        return body;
    }
    public GetTaskInstanceLogResponseBody log(GetTaskInstanceLogRequest request) {
        return invoke("GetTaskInstanceLog",
                () -> provider.getClient().getTaskInstanceLog(request), GetTaskInstanceLogResponse::getStatusCode, GetTaskInstanceLogResponse::getBody);
    }

    private <R extends Response, B> B invoke(String action, Supplier<CompletableFuture<R>> call,
                                             Function<R, Integer> statusReader, Function<R, B> bodyReader) {
        CompletableFuture<R> future = null;
        try {
            future = call.get();
            R response = future.get(properties.getCallTimeoutSeconds(), TimeUnit.SECONDS);
            Integer status = response == null ? null : statusReader.apply(response);
            if (status == null || status < 200 || status >= 300) {
                throw new DataWorksException(502, "UPSTREAM_HTTP_ERROR", action + " HTTP 响应异常", null);
            }
            B body = bodyReader.apply(response);
            requireData(body, null);
            return body;
        } catch (TimeoutException e) {
            if (future != null) future.cancel(true);
            throw new DataWorksException(504, "DATAWORKS_TIMEOUT",
                    action + " 超时；写操作结果可能未知，请先核对云端结果再重试", null);
        } catch (InterruptedException e) {
            if (future != null) future.cancel(true);
            Thread.currentThread().interrupt();
            throw new DataWorksException(503, "REQUEST_INTERRUPTED", action + " 等待被中断", null);
        } catch (ExecutionException | CompletionException e) {
            throw upstream(action, e.getCause());
        } catch (DataWorksException e) {
            throw e;
        } catch (RuntimeException e) {
            throw upstream(action, e);
        }
    }

    private DataWorksException upstream(String action, Throwable e) {
        while ((e instanceof CompletionException || e instanceof ExecutionException) && e.getCause() != null) {
            e = e.getCause();
        }
        String code = "DATAWORKS_SDK_ERROR";
        String requestId = null;
        if (e instanceof PopClientException) {
            code = ((PopClientException) e).getErrCode();
            requestId = ((PopClientException) e).getRequestId();
        } else if (e instanceof PopServerException) {
            code = ((PopServerException) e).getErrCode();
            requestId = ((PopServerException) e).getRequestId();
        }
        return new DataWorksException(502, code == null ? "DATAWORKS_SDK_ERROR" : code,
                action + " 调用失败，请按 code 和 requestId 排查；写操作请先核对云端结果", requestId);
    }
    private void requireSuccess(Boolean success, String code, String requestId) {
        if (!Boolean.TRUE.equals(success)) {
            throw new DataWorksException(502, code == null ? "DATAWORKS_REJECTED" : code,
                    "DataWorks 返回业务失败，请按 code 和 requestId 排查", requestId);
        }
    }
    private void requireData(Object value, String requestId) {
        if (value == null) throw new DataWorksException(502, "INVALID_UPSTREAM_RESPONSE",
                "DataWorks 返回缺少必要数据", requestId);
    }
}
