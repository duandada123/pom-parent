package com.sinogale.dksdkdemo.service;

import com.aliyun.dataworks_20240902.models.CreateFileRequest;
import com.aliyun.dataworks_20240902.models.CreateFileResponseBody;
import com.aliyun.dataworks_20240902.models.DeployFileRequest;
import com.aliyun.dataworks_20240902.models.DeployFileResponseBody;
import com.aliyun.dataworks_20240902.models.GetDeploymentRequest;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponseBody;
import com.aliyun.dataworks_20240902.models.SubmitFileRequest;
import com.aliyun.dataworks_20240902.models.SubmitFileResponseBody;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import org.springframework.stereotype.Service;

/** 文件生命周期编排；使用 DataWorks 20240902 SDK，不隐式重试写操作。 */
@Service
public class DataWorksService {
    private final DataWorksGateway gateway;
    private final DataWorksProperties properties;

    public DataWorksService(DataWorksGateway gateway, DataWorksProperties properties) {
        this.gateway = gateway;
        this.properties = properties;
    }

    public Long projectId(Long supplied) {
        Long value = supplied == null ? properties.getProjectId() : supplied;
        if (value == null || value <= 0) {
            throw new DataWorksException(400, "PROJECT_ID_REQUIRED",
                    "请传入 projectId 或配置 DATAWORKS_PROJECT_ID", null);
        }
        return value;
    }

    public CreateScriptResponse create(CreateScriptRequest request) {
        CreateFileRequest sdkRequest = new CreateFileRequest()
                .setRegionId(properties.getRegionId())
                .setProjectId(projectId(request.getProjectId()))
                .setFileName(request.getFileName())
                .setFileType(request.getFileType())
                .setFileFolderPath(request.getFileFolderPath())
                .setContent(request.getContent())
                .setFileDescription(request.getFileDescription())
                .setConnectionName(request.getConnectionName())
                .setInputList(request.getInputList())
                .setParaValue(request.getParaValue())
                .setCronExpress(request.getCronExpress())
                .setCycleType(request.getCycleType())
                .setOwner(request.getOwner())
                .setResourceGroupId(request.getResourceGroupId())
                .setResourceGroupIdentifier(request.getResourceGroupIdentifier())
                .setAutoParsing(request.getAutoParsing())
                .setAutoRerunTimes(request.getAutoRerunTimes())
                .setAutoRerunIntervalMillis(request.getAutoRerunIntervalMillis());
        CreateFileResponseBody body = gateway.create(sdkRequest);
        CreateScriptResponse result = new CreateScriptResponse();
        result.setFileId(body.getData());
        result.setRequestId(body.getRequestId());
        return result;
    }

    public SubmitScriptResponse submit(SubmitScriptRequest request) {
        SubmitFileRequest sdkRequest = new SubmitFileRequest()
                .setRegionId(properties.getRegionId())
                .setProjectId(projectId(request.getProjectId()))
                .setFileId(request.getFileId())
                .setComment(request.getComment());
        SubmitFileResponseBody body = gateway.submit(sdkRequest);
        SubmitScriptResponse result = new SubmitScriptResponse();
        result.setFileId(request.getFileId());
        result.setDeploymentId(body.getData());
        result.setRequestId(body.getRequestId());
        return result;
    }

    public DeployScriptResponse deploy(DeployScriptRequest request) {
        Long projectId = projectId(request.getProjectId());
        GetDeploymentRequest query = new GetDeploymentRequest()
                .setRegionId(properties.getRegionId())
                .setProjectId(projectId)
                .setDeploymentId(request.getDeploymentId());
        GetDeploymentResponseBody body = gateway.deployment(query);
        GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                body.getData().getDeployment();
        if (deployment.getStatus() == null) {
            throw new DataWorksException(502, "INVALID_UPSTREAM_RESPONSE",
                    "发布包缺少 status", body.getRequestId());
        }

        DeployScriptResponse result = new DeployScriptResponse();
        result.setFileId(request.getFileId());
        result.setDeploymentId(request.getDeploymentId());
        result.setStatus(deployment.getStatus());
        result.setCheckingStatus(deployment.getCheckingStatus());
        result.setToEnvironment(deployment.getToEnvironment());
        result.setRequestId(body.getRequestId());

        if (request.isCheckOnly()) {
            result.setState("STATUS_ONLY");
            result.setMessage("仅查询发布包；status=0 就绪，1 成功，2 失败；toEnvironment=1 开发，2 生产");
            return result;
        }
        if (!Integer.valueOf(1).equals(deployment.getToEnvironment())) {
            throw new DataWorksException(409, "NOT_A_SUBMISSION_PACKAGE",
                    "发布前必须提供提交到开发环境的 deploymentId；生产发布包请使用 checkOnly 查询",
                    body.getRequestId());
        }
        if (Integer.valueOf(2).equals(deployment.getStatus())) {
            result.setState("SUBMIT_FAILED");
            result.setMessage("提交失败，请查看 DataWorks 发布包详情");
        } else if (Integer.valueOf(7).equals(deployment.getCheckingStatus())) {
            result.setState("CHECK_FAILED");
            result.setMessage("发布前检查未通过");
        } else if (!Integer.valueOf(1).equals(deployment.getStatus())
                || deployment.getCheckingStatus() != null) {
            result.setState("WAITING");
            result.setMessage("提交或发布前检查尚未完成，请稍后再次调用");
        } else {
            DeployFileRequest deployRequest = new DeployFileRequest()
                    .setRegionId(properties.getRegionId())
                    .setProjectId(projectId)
                    .setFileId(request.getFileId())
                    .setComment(request.getComment());
            DeployFileResponseBody published = gateway.deploy(deployRequest);
            result.setPublishDeploymentId(published.getData());
            result.setRequestId(published.getRequestId());
            result.setState("PUBLISH_ACCEPTED");
            result.setMessage("发布请求已受理；用 publishDeploymentId 作为 deploymentId，checkOnly=true 查询最终状态");
        }
        return result;
    }
}
