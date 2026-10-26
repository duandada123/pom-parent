package com.sinogale.dksdkdemo.service;

import com.aliyun.dataworks_20240902.models.DeployFileResponse;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponse;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponseBody;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import com.sinogale.dksdkdemo.exception.DataWorksApiException;
import org.springframework.stereotype.Service;

/**
 * 创建文件、提交、检查提交状态并发布的组合服务。
 * 单个 DataWorks 能力仍由 DataWorksService 提供。
 */
@Service
public class DataWorksFileLifecycleService {

    private static final int MAX_CHECK_ATTEMPTS = 10;
    private static final long CHECK_INTERVAL_MILLIS = 3000L;

    private final DataWorksService dataWorksService;

    public DataWorksFileLifecycleService(DataWorksService dataWorksService) {
        this.dataWorksService = dataWorksService;
    }

    /**
     * 按创建、提交、检查提交状态、发布的顺序执行完整流程。
     * 返回发布接口响应，其中 body.data 是发布生成的新 deploymentId。
     */
    public DeployFileResponse createSubmitCheckAndPublish(CreateScriptRequest request) {
        CreateScriptResponse createResult = dataWorksService.create(request);

        SubmitScriptRequest submitRequest = new SubmitScriptRequest();
        submitRequest.setFileId(createResult.getFileId());
        SubmitScriptResponse submitResult = dataWorksService.submit(submitRequest);

        String lastRequestId = submitResult.getRequestId();
        for (int attempt = 1; attempt <= MAX_CHECK_ATTEMPTS; attempt++) {
            GetDeploymentResponse statusResponse =
                    dataWorksService.getDeployment(submitResult.getDeploymentId());
            GetDeploymentResponseBody statusBody = statusResponse.getBody();
            lastRequestId = statusBody == null ? lastRequestId : statusBody.getRequestId();
            GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                    statusBody == null || statusBody.getData() == null
                            ? null : statusBody.getData().getDeployment();
            if (deployment == null) {
                throw new DataWorksApiException("INVALID_UPSTREAM_RESPONSE",
                        "查询提交状态成功，但 DataWorks 返回的 deployment 为空", lastRequestId);
            }

            if (Integer.valueOf(1).equals(deployment.getStatus())) {
                return dataWorksService.deployFile(createResult.getFileId(), null);
            }
            if (Integer.valueOf(2).equals(deployment.getStatus())) {
                throw new DataWorksApiException("SUBMIT_FAILED",
                        deployment.getErrorMessage() == null
                                ? "DataWorks 文件提交失败" : deployment.getErrorMessage(),
                        lastRequestId);
            }

            if (attempt < MAX_CHECK_ATTEMPTS) {
                try {
                    Thread.sleep(CHECK_INTERVAL_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new DataWorksApiException("SUBMIT_STATUS_CHECK_INTERRUPTED",
                            "等待 DataWorks 提交状态时线程被中断", lastRequestId);
                }
            }
        }

        throw new DataWorksApiException("SUBMIT_STATUS_CHECK_TIMEOUT",
                "查询 DataWorks 提交状态超时，deploymentId=" + submitResult.getDeploymentId(),
                lastRequestId);
    }
}
