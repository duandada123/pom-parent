package com.sinogale.dksdkdemo.service;

import com.aliyun.sdk.service.dataworks_public20240518.models.*;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.*;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import com.sinogale.dksdkdemo.sdk.DataWorksGateway;
import org.springframework.stereotype.Service;
import java.util.Objects;

/** 文件生命周期编排；不使用反射、不固定 sleep、不隐式重试写操作。 */
@Service
public class DataWorksService {
    private final DataWorksGateway gateway;
    private final DataWorksProperties properties;
    public DataWorksService(DataWorksGateway gateway, DataWorksProperties properties) {
        this.gateway = gateway; this.properties = properties;
    }

    public Long projectId(Long supplied) {
        Long value = supplied == null ? properties.getProjectId() : supplied;
        if (value == null || value <= 0) throw new DataWorksException(400, "PROJECT_ID_REQUIRED",
                "请传入 projectId 或配置 DATAWORKS_PROJECT_ID", null);
        return value;
    }

    public CreateScriptResponse create(CreateScriptRequest request) {
        CreateFileResponseBody body = gateway.create(CreateFileRequest.builder()
                .regionId(properties.getRegionId()).projectId(projectId(request.getProjectId()))
                .fileName(request.getFileName()).fileType(request.getFileType())
                .fileFolderPath(request.getFileFolderPath()).content(request.getContent())
                .fileDescription(request.getFileDescription()).connectionName(request.getConnectionName())
                .inputList(request.getInputList()).outputList(request.getOutputList())
                .paraValue(request.getParaValue()).cronExpress(request.getCronExpress())
                .cycleType(request.getCycleType()).owner(request.getOwner())
                .resourceGroupId(request.getResourceGroupId())
                .resourceGroupIdentifier(request.getResourceGroupIdentifier())
                .createFolderIfNotExists(request.getCreateFolderIfNotExists())
                .autoParsing(request.getAutoParsing()).autoRerunTimes(request.getAutoRerunTimes())
                .autoRerunIntervalMillis(request.getAutoRerunIntervalMillis()).build());
        CreateScriptResponse result = new CreateScriptResponse();
        result.setFileId(body.getData()); result.setRequestId(body.getRequestId());
        return result;
    }

    public SubmitScriptResponse submit(SubmitScriptRequest request) {
        SubmitFileResponseBody body = gateway.submit(SubmitFileRequest.builder()
                .regionId(properties.getRegionId()).projectId(projectId(request.getProjectId()))
                .fileId(request.getFileId()).comment(request.getComment())
                .skipAllDeployFileExtensions(false).build());
        SubmitScriptResponse result = new SubmitScriptResponse();
        result.setFileId(request.getFileId()); result.setDeploymentId(body.getData());
        result.setRequestId(body.getRequestId());
        return result;
    }

    public DeployScriptResponse deploy(DeployScriptRequest request) {
        Long projectId = projectId(request.getProjectId());
        GetDeploymentPackageResponseBody body = gateway.deployment(GetDeploymentPackageRequest.builder()
                .regionId(properties.getRegionId()).projectId(projectId)
                .deploymentId(request.getDeploymentId()).build());
        GetDeploymentPackageResponseBody.Deployment deployment = body.getData().getDeployment();
        if (deployment.getStatus() == null) throw new DataWorksException(502,
                "INVALID_UPSTREAM_RESPONSE", "发布包缺少 status", body.getRequestId());
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
                    "发布前必须提供提交到开发环境的 deploymentId；生产发布包请使用 checkOnly 查询", body.getRequestId());
        }
        if (Integer.valueOf(2).equals(deployment.getStatus())) {
            result.setState("SUBMIT_FAILED");
            result.setMessage("提交失败，请查看 DataWorks 发布包详情");
        } else if (Integer.valueOf(7).equals(deployment.getCheckingStatus())) {
            result.setState("CHECK_FAILED");
            result.setMessage("发布前检查未通过");
        } else if (!Integer.valueOf(1).equals(deployment.getStatus()) || deployment.getCheckingStatus() != null) {
            result.setState("WAITING");
            result.setMessage("提交或发布前检查尚未完成，请稍后再次调用");
        } else {
            // 就绪包可能尚无文件明细；只在实际写入前校验文件归属。
            if (body.getData().getDeployedItems() == null || body.getData().getDeployedItems().stream()
                    .noneMatch(item -> Objects.equals(request.getFileId(), item.getFileId()))) {
                throw new DataWorksException(409, "FILE_DEPLOYMENT_MISMATCH",
                        "发布包中未找到该 fileId，请使用此文件对应的提交 deploymentId", body.getRequestId());
            }
            // FileId 与 NodeId 二选一即可，省去发布前查节点的额外请求。
            DeployFileResponseBody published = gateway.deploy(DeployFileRequest.builder()
                    .regionId(properties.getRegionId()).projectId(projectId)
                    .fileId(request.getFileId()).comment(request.getComment()).build());
            result.setPublishDeploymentId(published.getData()); result.setRequestId(published.getRequestId());
            result.setState("PUBLISH_ACCEPTED");
            result.setMessage("发布请求已受理；用 publishDeploymentId 作为 deploymentId，checkOnly=true 查询最终状态");
        }
        return result;
    }
}
