package com.sinogale.dksdkdemo.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.dataworks_20240902.models.*;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.CreateScriptDto;
import com.sinogale.dksdkdemo.dto.DeployScriptDto;
import com.sinogale.dksdkdemo.dto.QueryLogsDto;
import com.sinogale.dksdkdemo.dto.SubmitScriptDto;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.entity.DwInstanceLogDetail;
import com.sinogale.dksdkdemo.exception.DataWorksApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@Service
@Log4j2
public class DataWorksService {

    private final DataWorksSdkInvoker dataWorksSdkInvoker;

    private final DataWorksProperties properties;

    public DataWorksService(DataWorksSdkInvoker dataWorksSdkInvoker, DataWorksProperties properties) {
        this.dataWorksSdkInvoker = dataWorksSdkInvoker;
        this.properties = properties;
    }


    public ApiResponse<CreateScriptResponse> create(CreateScriptDto dto) {
        validateDto(dto);
        Map<String, Object> result = dataWorksSdkInvoker
                .executeFunction("CreateFile", createFileParams(dto));
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


    // 提交文件
    public ApiResponse<SubmitScriptResponse> submit(SubmitScriptDto dto) {
        validateDto(dto);
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("FileId", dto.getFileId());
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("SubmitFile", params);
        SubmitFileResponse response = null;
        if ((Boolean) result.get("success")) {
            response =  (SubmitFileResponse) result.get("response");
            //发布包的ID，后续需要将该ID作为参数调用GetDeployment接口，获取本次发布的具体情况。
        } else {
            log.info("提交文件失败(提交的FileId: " + dto.getFileId() + "_" + "): " + result.get("errMsg"));
        }
        SubmitFileResponseBody body = response == null ? null : response.getBody();
        requireSuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "提交脚本",
                body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        SubmitScriptResponse data = new SubmitScriptResponse();
        data.setDeploymentId(body.getData());
        data.setRequestId(body.getRequestId());
        data.setState("SUBMIT_ACCEPTED");
        return ApiResponse.ok(data, body.getRequestId());
    }


    //获取节点信息
    public ListNodesResponseBody.ListNodesResponseBodyData getNodesInfo(String nodeName) {
        int pageSize = 10;
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectEnv", properties.getProjectEnv());
        params.put("ProjectId", properties.getProjectId());
//        params.put("Owner", "5229294322010124193");
        //PYODPS3 ODPS_SQL ODPSSPARK
        params.put("ProgramType", "PYODPS3");
        params.put("NodeName", nodeName);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("ListNodes", params);
        if (!(Boolean) result.get("success")) {
            log.error("获取文件详情失败(FileId: " + nodeName + "): " + result.get("errMsg"));
            return null;
        }
        ListNodesResponse response = (ListNodesResponse) result.get("response");
        ListNodesResponseBody.ListNodesResponseBodyData nodeData = response.getBody().getData();
        return nodeData;
    }

    // 获取文件详情
    public GetFileResponse getFileDetail(Long fileId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("FileId", fileId);
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("GetFile", params);
        if (!(Boolean) result.get("success")) {
            log.error("获取文件详情失败(FileId: " + fileId + "): " + result.get("errMsg"));
            return null;
        }
        return (GetFileResponse) result.get("response");
    }


    //用GetDeployment接口获取文件的提交状态
    public GetDeploymentResponse getDeployment(Long deploymentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("DeploymentId", deploymentId);

        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("GetDeployment", params);
        if ((Boolean) result.get("success")) {
            GetDeploymentResponse response = (GetDeploymentResponse) result.get("response");
            return response;
        } else {
            log.error("getDeployment失败(提交的FileId: " + deploymentId + "_" + "): " + result.get("errMsg"));
            return null;
        }
    }


    //用GetDeployment接口获取文件的提交状态
    public DeployFileResponse deployFile(Long fileId, Long nodeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        if (null != fileId) {
            params.put("FileId", fileId);
        }
        if (null != nodeId) {
            params.put("NodeId", nodeId);
        }
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("DeployFile", params);
        if ((Boolean) result.get("success")) {
            DeployFileResponse response = (DeployFileResponse) result.get("response");
            return response;
        } else {
            log.error("提交文件失败(发布的FileId: " + fileId + "_" + "): " + result.get("errMsg"));
            return null;
        }
    }


    public ApiResponse<DeployScriptResponse> deploy(DeployScriptDto dto) {
        validateDto(dto);
        GetDeploymentResponse statusResponse = this.getDeployment(dto.getDeploymentId());
        GetDeploymentResponseBody statusBody = statusResponse == null ? null : statusResponse.getBody();
        requireSuccess(statusBody != null && Boolean.TRUE.equals(statusBody.getSuccess()), "查询发布包状态",
                statusBody == null ? null : statusBody.getErrorMessage(), statusBody == null ? null : statusBody.getRequestId());
        GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                statusBody.getData() == null ? null : statusBody.getData().getDeployment();
        if (deployment == null) {
            throw upstreamFailure("查询发布包状态失败", "DataWorks 返回的 deployment 为空", statusBody.getRequestId());
        }
        DeployScriptResponse data = deploymentResponse(dto.getDeploymentId(), statusBody.getRequestId(), deployment);
        if (dto.isCheckOnly()) {
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
            throw new DataWorksApiException("NOT_A_SUBMISSION_PACKAGE",
                    "当前 deploymentId 不是开发环境提交包，不能据此再次发布", statusBody.getRequestId());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("NodeId", dto.getNodeId());
        params.put("FileId", dto.getFileId());
        params.put("ProjectId", properties.getProjectId());
        DeployFileResponse publishResponse = null;
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("DeployFile", params);
        if ((Boolean) result.get("success")) {
            publishResponse = (DeployFileResponse) result.get("response");
        } else {
            log.error("提交文件失败(发布的FileId: " + dto.getFileId() + "_" + "): " + result.get("errMsg"));
        }
        DeployFileResponseBody publishBody = publishResponse == null ? null : publishResponse.getBody();
        requireSuccess(publishBody != null && Boolean.TRUE.equals(publishBody.getSuccess()), "发布脚本",
                publishBody == null ? null : publishBody.getErrorMessage(), publishBody == null ? null : publishBody.getRequestId());
        data.setState("PUBLISH_ACCEPTED");
        data.setPublishDeploymentId(publishBody.getData());
        data.setPublishRequestId(publishBody.getRequestId());
        return ApiResponse.ok(data, publishBody.getRequestId());
    }


    //    调用ListNodes获取节点的列表。
    public void getNodesLog(String nodeName) {
        int pageSize = 10;
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectEnv", properties.getProjectEnv());
        params.put("ProjectId", properties.getProjectId());
        params.put("RegionId", properties.getRegionId());
        params.put("ProgramType", "PYODPS3");
        params.put("NodeName", nodeName);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);
        Map<String, Object> listNodesResult = dataWorksSdkInvoker.executeFunction("ListNodes", params);
        if ((boolean) listNodesResult.get("success")) {
            ListNodesResponse response = (ListNodesResponse) listNodesResult.get("response");
            ListNodesResponseBody.ListNodesResponseBodyData dataList = response.getBody().getData();
            Integer totalCount = dataList.getTotalCount();
            int pageTotal = (totalCount + pageSize - 1) / pageSize;
            for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
                params.put("PageNumber", folderPage);
                if (folderPage > 1) {
                    listNodesResult = dataWorksSdkInvoker.executeFunction("ListNodes", params);
                }
                if ((boolean) listNodesResult.get("success")) {
                    response = (ListNodesResponse) listNodesResult.get("response");
                    List<ListNodesResponseBody.ListNodesResponseBodyDataNodes> nodes = response.getBody().getData().getNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        ListNodesResponseBody.ListNodesResponseBodyDataNodes node = nodes.get(i);
                        log.info(node.getNodeName() + "--" + node.getNodeId());
                        log.info(JSON.toJSONString(node));
                        getListInstanceLog(node.getNodeId());
                    }
                }
            }
        } else {
            log.error("ListNodes Failed, Error Code: " + listNodesResult.get("errCode"));
            log.error("Error Message: " + listNodesResult.get("errMsg"));
            log.error("Request ID: " + listNodesResult.get("requestId"));
        }
    }
    
    //GetInstance 调用GetInstance获取实例的详情。
    public void getInstance(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());
        Map<String, Object> getInstanceResult = dataWorksSdkInvoker.executeFunction("GetInstance", params);
        if ((boolean) getInstanceResult.get("success")) {
            GetInstanceResponse response = (GetInstanceResponse) getInstanceResult.get("response");
            GetInstanceResponseBody.GetInstanceResponseBodyData data = response.getBody().getData();
            log.info("InstanceResponseBodyData: \n" + JSON.toJSONString(data));
        } else {
            log.error("CreateManualDag Failed, Error Code: " + getInstanceResult.get("errCode"));
            log.error("Error Message: " + getInstanceResult.get("errMsg"));
            log.error("Request ID: " + getInstanceResult.get("requestId"));
        }
    }

    public ApiResponse<QueryLogsResponse> logs(QueryLogsDto dto) {
        validateDto(dto);
        QueryLogsResponse data = new QueryLogsResponse();
        data.setInstanceId(dto.getInstanceId());
        data.setNodeId(dto.getNodeId());
        if (dto.getInstanceId() != null) {
            data.setLogContent(getInstanceLog(dto.getInstanceId()));
        } else {
            data.setLogs(getListInstanceLog(dto.getNodeId()));
        }
        return ApiResponse.ok(data);
    }





    //    GetInstanceLog  调用GetInstanceLog获取实例的日志。
    public String getInstanceLog(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());
        String message = "";
        Map<String, Object> getInstanceLogResult = dataWorksSdkInvoker.executeFunction("GetInstanceLog", params);
        if ((boolean) getInstanceLogResult.get("success")) {
            GetInstanceLogResponse response = (GetInstanceLogResponse) getInstanceLogResult.get("response");
            message = response.getBody().getData();
        } else {
            log.error("getInstanceLog Failed, Error Code: " + getInstanceLogResult.get("errCode"));
            log.error("Error Message: " + getInstanceLogResult.get("errMsg"));
            log.error("Request ID: " + getInstanceLogResult.get("requestId"));
        }
        return message;
    }

    //调用ListInstances获取实例的列表。
    public List<DwInstanceLogDetail> getListInstanceLog(Long nodeId) {
        String bizdate = DateUtil.format(DateUtil.beginOfDay(DateUtil.yesterday()), "yyyy-MM-dd HH:mm:ss");
        int pageSize = 20;
        Map<String, Object> params = new HashMap<>();
        params.put("NodeId", nodeId);
        params.put("ProjectEnv", properties.getProjectEnv());
        params.put("ProjectId",properties.getProjectId() );
        //只查T-1的最新的业务时间的实例
        params.put("Bizdate", bizdate);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);
        List<DwInstanceLogDetail> logList = new ArrayList<>();
        Map<String, Object> listInstancesResult = dataWorksSdkInvoker.executeFunction("ListInstances", params);
        if ((boolean) listInstancesResult.get("success")) {
            ListInstancesResponse response = (ListInstancesResponse) listInstancesResult.get("response");
            ListInstancesResponseBody.ListInstancesResponseBodyData dataList = response.getBody().getData();
            Integer totalCount = dataList.getTotalCount();
            int pageTotal = (totalCount + pageSize - 1) / pageSize;
            DwInstanceLogDetail logDetail = null;
            for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
                params.put("PageNumber", folderPage);
                if (folderPage > 1) {
                    listInstancesResult = dataWorksSdkInvoker.executeFunction("ListInstances", params);
                }
                if ((boolean) listInstancesResult.get("success")) {
                    response = (ListInstancesResponse) listInstancesResult.get("response");
                    List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances = response.getBody().getData().getInstances();
                    for (int i = 0; i < instances.size(); i++) {
                        ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instanceDetail = instances.get(i);
                        String beginRunningTime = formatTime(instanceDetail.getBeginRunningTime(), "yyyy-MM-dd HH:mm:ss");
                        String finishTime = formatTime(instanceDetail.getFinishTime(), "yyyy-MM-dd HH:mm:ss");
                        logDetail = new DwInstanceLogDetail();
                        logDetail.setBeginRunningTime(beginRunningTime)
                                .setBeginWaitResTime(formatTime(instanceDetail.getBeginWaitResTime(), "yyyy-MM-dd HH:mm:ss"))
                                .setBeginWaitTimeTime(formatTime(instanceDetail.getBeginWaitTimeTime(), "yyyy-MM-dd HH:mm:ss"))
                                .setBizdate(formatTime(instanceDetail.getBizdate(), "yyyyMMdd"))
                                .setCycTime(formatTime(instanceDetail.getCycTime(), "yyyy-MM-dd HH:mm:ss"))
                                .setDagId(instanceDetail.getDagId())
                                .setDqcType(instanceDetail.getDqcType())
                                .setFinishTime(finishTime)
                                .setInstanceId(instanceDetail.getInstanceId())
                                .setNodeId(instanceDetail.getNodeId())
                                .setNodeName(instanceDetail.getNodeName())
                                .setParamValues(instanceDetail.getParamValues())
                                .setStatus(instanceDetail.getStatus())
                                .setErrorMessage(instanceDetail.getErrorMessage());
                        String instanceLog = getInstanceLog(instanceDetail.getInstanceId());
                        logDetail.setLogContent(instanceLog);
                        log.info(JSON.toJSONString(instanceLog));
                        logList.add(logDetail);
                    }
                }
            }
        } else {
            log.error("getListInstanceLog Failed, Error Code: " + listInstancesResult.get("errCode"));
            log.error("Error Message: " + listInstancesResult.get("errMsg"));
            log.error("Request ID: " + listInstancesResult.get("requestId"));
        }
        return logList;
    }



    public void updateNodeRunMode(Long nodeId, int schedulerType) {
        Map<String, Object> updateNodeRunModeParams = new HashMap<>();
        updateNodeRunModeParams.put("NodeId", nodeId);
        updateNodeRunModeParams.put("ProjectEnv", properties.getProjectEnv());
        //0：表示解冻目标节点 。 2：表示冻结目标节点。
        updateNodeRunModeParams.put("SchedulerType", schedulerType);
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("UpdateNodeRunMode", updateNodeRunModeParams);
        // 输出结果
        String type = schedulerType == 0 ? "解冻" : "冻结";
        if ((Boolean) result.get("success")) {
            log.info(nodeId + "-" + type + "请求成功！");
            log.info("Response: " + JSON.toJSONString(result.get("response")));
        } else {
            log.error(nodeId + "-" + type + "请求失败！");
            log.error("Error: " + result.get("errorMessage"));
        }
    }


    //调用ListFiles查询文件列表。
    public GetFileResponseBody getDWFiles(String fileName) {
        Map<String, Object> listFilesParams = new HashMap<>();
        listFilesParams.put("ProjectId", properties.getProjectId());
        listFilesParams.put("UseType", "NORMAL");
        listFilesParams.put("Keyword", fileName);
        listFilesParams.put("PageNumber", 1);
        listFilesParams.put("PageSize", 10);
        Map<String, Object> ListFilesResult = dataWorksSdkInvoker.executeFunction("ListFiles", listFilesParams);
        if ((boolean) ListFilesResult.get("success")) {
            ListFilesResponse response = (ListFilesResponse) ListFilesResult.get("response");
            Integer pageSize = response.getBody().getData().getPageSize();
            Integer totalCount = response.getBody().getData().getTotalCount();
            Integer pageNumber = response.getBody().getData().getPageNumber();
            log.info(pageSize + "---" + totalCount + "---" + pageNumber);
            List<ListFilesResponseBody.ListFilesResponseBodyDataFiles> files = response.getBody().getData().getFiles();
            for (int i = 0; i < files.size(); i++) {
                Map<String, Object> getFileParams = new HashMap<>();
                getFileParams.put("ProjectId", properties.getProjectId());
                getFileParams.put("FileId", files.get(i).getFileId());
                Map<String, Object> getFileResult = dataWorksSdkInvoker.executeFunction("GetFile", getFileParams);
                if ((boolean) getFileResult.get("success")) {
                    GetFileResponse getFileresponse = (GetFileResponse) getFileResult.get("response");
                    GetFileResponseBody body = getFileresponse.getBody();
                    return body;
                }
            }
        } else {
            log.error("CreateManualDag Failed, Error Code: " + ListFilesResult.get("errCode"));
            log.error("Error Message: " + ListFilesResult.get("errMsg"));
            log.error("Request ID: " + ListFilesResult.get("requestId"));
            return null;
        }
        return null;
    }

    private Map<String, Object> createFileParams(CreateScriptDto dto) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("FileName", dto.getFileName());
        params.put("FileType", dto.getFileType());
        params.put("FileFolderPath", dto.getFileFolderPath());
        params.put("Content", dto.getContent());
        params.put("FileDescription", dto.getFileDescription());
        params.put("Owner", dto.getOwner());
        params.put("ConnectionName", dto.getConnectionName());
        params.put("CronExpress", dto.getCronExpress());
        params.put("CycleType", dto.getCycleType());
        params.put("DependentNodeIdList", dto.getDependentNodeIdList());
        params.put("DependentType", dto.getDependentType());
        params.put("ParaValue", dto.getParaValue());
        params.put("InputList", dto.getInputList());
        params.put("ResourceGroupId", dto.getResourceGroupId());
        params.put("ResourceGroupIdentifier", dto.getResourceGroupIdentifier());
        params.put("SchedulerType", dto.getSchedulerType());
        params.put("StartEffectDate", dto.getStartEffectDate());
        params.put("EndEffectDate", dto.getEndEffectDate());
        params.put("AutoParsing", dto.getAutoParsing());
        params.put("AutoRerunTimes", dto.getAutoRerunTimes());
        params.put("AutoRerunIntervalMillis", dto.getAutoRerunIntervalMillis());
        params.put("RerunMode", dto.getRerunMode());
        params.put("Stop", dto.getStop());
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

    private String formatTime(Long timestamp, String pattern) {
        return timestamp == null ? "" : DateUtil.format(DateUtil.date(timestamp), pattern);
    }


    private void requireSuccess(boolean success, String operation, String message, String requestId) {
        if (!success) {
            throw upstreamFailure(operation + "失败", message, requestId);
        }
    }

    private DataWorksApiException upstreamFailure(String fallback, Object detail, Object requestId) {
        String message = detail == null || String.valueOf(detail).trim().isEmpty() ? fallback : String.valueOf(detail);
        return new DataWorksApiException("DATAWORKS_ERROR", message,
                requestId == null ? null : String.valueOf(requestId));
    }

    private <T> T castResponse(Object response, Class<T> type, String operation) {
        if (!type.isInstance(response)) {
            throw upstreamFailure(operation + "失败", "DataWorks 返回类型异常", null);
        }
        return type.cast(response);
    }

    private <T> void validateDto(T dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto 不能为空");
        }
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
