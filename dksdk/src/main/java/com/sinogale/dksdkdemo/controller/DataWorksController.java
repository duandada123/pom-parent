package com.sinogale.dksdkdemo.controller;

import com.aliyun.dataworks_20240902.models.CreateFileResponse;
import com.aliyun.dataworks_20240902.models.CreateFileResponseBody;
import com.aliyun.dataworks_20240902.models.DeployFileResponse;
import com.aliyun.dataworks_20240902.models.DeployFileResponseBody;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponse;
import com.aliyun.dataworks_20240902.models.GetDeploymentResponseBody;
import com.aliyun.dataworks_20240902.models.SubmitFileResponse;
import com.aliyun.dataworks_20240902.models.SubmitFileResponseBody;
import com.sinogale.dksdkdemo.config.ConfigKeys;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.ApiResponse;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import com.sinogale.dksdkdemo.exception.DataWorksApiException;
import com.sinogale.dksdkdemo.service.DataWorksService;
import com.sinogale.dksdkdemo.util.InstanceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Api(tags = "DataWorks 脚本管理")
@RestController
@RequestMapping("/api/dataworks")
public class DataWorksController {

    private DataWorksService dataWorksV2Service;
    private InstanceUtil instanceUtil;
    private DataWorksProperties properties;

    public DataWorksController(DataWorksService dataWorksV2Service, InstanceUtil instanceUtil) {
        this.dataWorksV2Service = dataWorksV2Service;
        this.instanceUtil = instanceUtil;
    }

    @PostMapping("/files/create")
    @ApiOperation(value = "创建脚本", notes = "创建 DataWorks 文件，不会自动提交或发布。")
    public ApiResponse<CreateScriptResponse> create(@Valid @RequestBody CreateScriptRequest request) {
            Map<String, Object> result = dataWorksV2Service
                    .executeFunction("CreateFile", createFileParams(request));
            if (!Boolean.TRUE.equals(result.get("success"))) {
                throw upstreamFailure("创建脚本失败", result.get("errorMessage"), result.get("requestId"));
            }
            CreateFileResponse response = castResponse(result.get("response"), CreateFileResponse.class, "创建脚本");
            CreateFileResponseBody body = response.getBody();
            requireSuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "创建脚本",
                    body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());

            CreateScriptResponse data = new CreateScriptResponse();
            data.setFileId(body.getData());
            data.setRequestId(body.getRequestId());
            return ApiResponse.ok(data, body.getRequestId());
    }

    @PostMapping("/files/submit")
    @ApiOperation(value = "提交脚本", notes = "提交文件并返回 deploymentId；提交成功后仍需检查发布包状态。")
    public ApiResponse<SubmitScriptResponse> submit(@Valid @RequestBody SubmitScriptRequest request) {
            SubmitFileResponse response = dataWorksV2Service.submitFile(request.getFileId());
            SubmitFileResponseBody body = response == null ? null : response.getBody();
            requireSuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "提交脚本",
                    body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());

            SubmitScriptResponse data = new SubmitScriptResponse();
            data.setDeploymentId(body.getData());
            data.setRequestId(body.getRequestId());
            data.setState("SUBMIT_ACCEPTED");
            return ApiResponse.ok(data, body.getRequestId());
    }

    @PostMapping("/files/deploy")
    @ApiOperation(value = "检查状态并发布脚本", notes = "checkOnly=true 时仅查询状态；否则状态满足条件后发布 fileId 或 nodeId。")
    public ApiResponse<DeployScriptResponse> deploy(@Valid @RequestBody DeployScriptRequest request) {
            DataWorksService service = dataWorksV2Service;
            GetDeploymentResponse statusResponse = service.getDeployment(request.getDeploymentId());
            GetDeploymentResponseBody statusBody = statusResponse == null ? null : statusResponse.getBody();
            requireSuccess(statusBody != null && Boolean.TRUE.equals(statusBody.getSuccess()), "查询发布包状态",
                    statusBody == null ? null : statusBody.getErrorMessage(), statusBody == null ? null : statusBody.getRequestId());

            GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                    statusBody.getData() == null ? null : statusBody.getData().getDeployment();
            if (deployment == null) {
                throw upstreamFailure("查询发布包状态失败", "DataWorks 返回的 deployment 为空", statusBody.getRequestId());
            }

            DeployScriptResponse data = deploymentResponse(request.getDeploymentId(), statusBody.getRequestId(), deployment);
            if (request.isCheckOnly()) {
                data.setState("STATUS_ONLY");
                return ApiResponse.ok(data, statusBody.getRequestId());
            }
            if (deployment.getStatus() == null || deployment.getStatus() == 0) {
                data.setState("WAITING");
                return ApiResponse.ok(data, statusBody.getRequestId());
            }
            if (deployment.getStatus() != 1) {
                data.setState("SUBMIT_FAILED");
                return ApiResponse.ok(data, statusBody.getRequestId());
            }
            if (deployment.getCheckingStatus() != null) {
                data.setState(deployment.getCheckingStatus() == 7 ? "CHECK_FAILED" : "WAITING");
                return ApiResponse.ok(data, statusBody.getRequestId());
            }
            if (deployment.getToEnvironment() != null && deployment.getToEnvironment() != 1) {
                throw new DataWorksApiException(HttpStatus.CONFLICT, "NOT_A_SUBMISSION_PACKAGE",
                        "当前 deploymentId 不是开发环境提交包，不能据此再次发布", statusBody.getRequestId());
            }

            DeployFileResponse publishResponse = service.deployFile(request.getFileId(), request.getNodeId());
            DeployFileResponseBody publishBody = publishResponse == null ? null : publishResponse.getBody();
            requireSuccess(publishBody != null && Boolean.TRUE.equals(publishBody.getSuccess()), "发布脚本",
                    publishBody == null ? null : publishBody.getErrorMessage(), publishBody == null ? null : publishBody.getRequestId());
            data.setState("PUBLISH_ACCEPTED");
            data.setPublishDeploymentId(publishBody.getData());
            data.setPublishRequestId(publishBody.getRequestId());
            return ApiResponse.ok(data, publishBody.getRequestId());
    }

    @PostMapping("/logs/query")
    @ApiOperation(value = "查询运行日志", notes = "传 instanceId 查询单实例日志；传 nodeId 查询现有逻辑中的昨日实例及日志。")
    public ApiResponse<QueryLogsResponse> logs(@Valid @RequestBody QueryLogsRequest request) {
            QueryLogsResponse data = new QueryLogsResponse();
            data.setInstanceId(request.getInstanceId());
            data.setNodeId(request.getNodeId());
            if (request.getInstanceId() != null) {
                data.setLogContent(instanceUtil.getInstanceLog(request.getInstanceId()));
            } else {
                data.setLogs(instanceUtil.getListInstanceLog(request.getNodeId()));
            }
            return ApiResponse.ok(data);
    }

    private Map<String, Object> createFileParams(CreateScriptRequest request) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProjectId", 10022L);
        params.put( "FileName", request.getFileName());
        params.put("FileType", request.getFileType());
        params.put("FileFolderPath", request.getFileFolderPath());
        params.put("Content", request.getContent());
        params.put("FileDescription", request.getFileDescription());
        params.put("Owner", request.getOwner());
        params.put("ConnectionName", request.getConnectionName());
        params.put("CronExpress", request.getCronExpress());
        params.put("CycleType", request.getCycleType());
        params.put("DependentNodeIdList", request.getDependentNodeIdList());
        params.put("DependentType", request.getDependentType());
        params.put("ParaValue", request.getParaValue());
        params.put("InputList", request.getInputList());
        params.put("ResourceGroupId", request.getResourceGroupId());
        params.put("ResourceGroupIdentifier", request.getResourceGroupIdentifier());
        params.put("SchedulerType", request.getSchedulerType());
        params.put("StartEffectDate", request.getStartEffectDate());
        params.put("EndEffectDate", request.getEndEffectDate());
        params.put("AutoParsing", request.getAutoParsing());
        params.put("AutoRerunTimes", request.getAutoRerunTimes());
        params.put("AutoRerunIntervalMillis", request.getAutoRerunIntervalMillis());
        params.put("RerunMode", request.getRerunMode());
        params.put("Stop", request.getStop());
        return params;
    }



    private DeployScriptResponse deploymentResponse(Long deploymentId, String requestId,
                                                     GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment) {
        DeployScriptResponse data = new DeployScriptResponse();
        data.setDeploymentId(deploymentId);
        data.setStatus(deployment.getStatus());
        data.setCheckingStatus(deployment.getCheckingStatus());
        data.setFromEnvironment(deployment.getFromEnvironment());
        data.setToEnvironment(deployment.getToEnvironment());
        data.setErrorMessage(deployment.getErrorMessage());
        data.setStatusRequestId(requestId);
        return data;
    }

    private void requireSuccess(boolean success, String operation, String message, String requestId) {
        if (!success) {
            throw upstreamFailure(operation + "失败", message, requestId);
        }
    }

    private DataWorksApiException upstreamFailure(String fallback, Object detail, Object requestId) {
        String message = detail == null || String.valueOf(detail).trim().isEmpty() ? fallback : String.valueOf(detail);
        return new DataWorksApiException(HttpStatus.BAD_GATEWAY, "DATAWORKS_ERROR", message,
                requestId == null ? null : String.valueOf(requestId));
    }

    private <T> T castResponse(Object response, Class<T> type, String operation) {
        if (!type.isInstance(response)) {
            throw upstreamFailure(operation + "失败", "DataWorks 返回类型异常", null);
        }
        return type.cast(response);
    }

    private <T> T callSdk(SdkCall<T> call) {
        try {
            return call.execute();
        } catch (DataWorksApiException e) {
            throw e;
        } catch (Exception e) {
            throw upstreamFailure("DataWorks 调用失败", e.getMessage(), null);
        }
    }

    private interface SdkCall<T> {
        T execute() throws Exception;
    }
}
