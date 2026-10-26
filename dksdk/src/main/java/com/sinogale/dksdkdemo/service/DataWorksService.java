package com.sinogale.dksdkdemo.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.dataworks_20240902.models.*;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.QueryExecutionRequest;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.request.RunScriptRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.entity.DwInstanceLogDetail;
import com.sinogale.dksdkdemo.enums.DataWorksOperationState;
import com.sinogale.dksdkdemo.exception.DataWorksApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Log4j2
public class DataWorksService {

    private final DataWorksSdkInvoker dataWorksSdkInvoker;

    private final DataWorksProperties properties;

    private Validator validator;

    public DataWorksService(DataWorksSdkInvoker dataWorksSdkInvoker, DataWorksProperties properties,Validator validator) {
        this.dataWorksSdkInvoker = dataWorksSdkInvoker;
        this.properties = properties;
        this.validator = validator;
    }


    public CreateScriptResponse create(CreateScriptRequest request) {
        validateDto(request);
        Map<String, Object> result = dataWorksSdkInvoker
                .executeFunction("CreateFile", createFileParams(request));
        CreateFileResponse response = requireResponse(result, CreateFileResponse.class, "创建脚本");
        CreateFileResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "创建脚本",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        CreateScriptResponse data = new CreateScriptResponse();
        data.setFileId(body.getData());
        data.setRequestId(body.getRequestId());
        return data;
    }


    // 提交文件
    public SubmitScriptResponse submit(SubmitScriptRequest request) {
        validateDto(request);
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("RegionId", properties.getRegionId());
        params.put("FileId", request.getFileId());
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("SubmitFile", params);
        SubmitFileResponse response = requireResponse(result, SubmitFileResponse.class, "提交脚本");
        SubmitFileResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "提交脚本",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        SubmitScriptResponse data = new SubmitScriptResponse();
        data.setDeploymentId(body.getData());
        data.setRequestId(body.getRequestId());
        data.setState(DataWorksOperationState.SUBMIT_ACCEPTED);
        return data;
    }

    //获取节点信息
    public ListNodesResponseBody.ListNodesResponseBodyData getNodesInfo(String nodeName) {
        int pageSize = 10;
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectEnv", properties.getProjectEnv());
        params.put("ProjectId", properties.getProjectId());
        params.put("NodeName", nodeName);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("ListNodes", params);
        ListNodesResponse response = requireResponse(result, ListNodesResponse.class, "查询节点");
        ListNodesResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询节点",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        if (body.getData() == null) {
            return new ListNodesResponseBody.ListNodesResponseBodyData()
                    .setNodes(Collections.<ListNodesResponseBody.ListNodesResponseBodyDataNodes>emptyList())
                    .setTotalCount(0);
        }
        return body.getData();
    }

    // 获取文件详情
    public GetFileResponse getFileDetail(Long fileId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("FileId", fileId);
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("GetFile", params);
        GetFileResponse response = requireResponse(result, GetFileResponse.class, "获取文件详情");
        GetFileResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "获取文件详情",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        return response;
    }


    //用GetDeployment接口获取文件的提交状态
    public GetDeploymentResponse getDeployment(Long deploymentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("RegionId", properties.getRegionId());
        params.put("DeploymentId", deploymentId);

        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("GetDeployment", params);
        GetDeploymentResponse response = requireResponse(result, GetDeploymentResponse.class, "查询发布包状态");
        GetDeploymentResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询发布包状态",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        return response;
    }


    //用GetDeployment接口获取文件的提交状态
    //发布
    public DeployFileResponse deployFile(Long fileId, Long nodeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("RegionId", properties.getRegionId());
        if (null != fileId) {
            params.put("FileId", fileId);
        }
        if (null != nodeId) {
            params.put("NodeId", nodeId);
        }
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("DeployFile", params);
        DeployFileResponse response = requireResponse(result, DeployFileResponse.class, "发布脚本");
        DeployFileResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "发布脚本",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        return response;
    }

    //发布
    public DeployScriptResponse deploy(DeployScriptRequest request) {
        validateDto(request);
        GetDeploymentResponse statusResponse = this.getDeployment(request.getDeploymentId());
        GetDeploymentResponseBody statusBody = statusResponse.getBody();
        GetDeploymentResponseBody.GetDeploymentResponseBodyDataDeployment deployment =
                statusBody.getData() == null ? null : statusBody.getData().getDeployment();
        if (deployment == null) {
            throw new DataWorksApiException("INVALID_UPSTREAM_RESPONSE",
                    "查询发布包状态失败：DataWorks 返回的 deployment 为空", statusBody.getRequestId());
        }
        DeployScriptResponse data = deploymentResponse(request.getDeploymentId(), statusBody.getRequestId(), deployment);
        if (request.isCheckOnly()) {
            data.setState(DataWorksOperationState.STATUS_ONLY);
            return data;
        }
        if (deployment.getStatus() == null || deployment.getStatus() == 0) {
            data.setState(DataWorksOperationState.WAITING);
            return data;
        }
        if (deployment.getStatus() != 1) {
            data.setState(DataWorksOperationState.SUBMIT_FAILED);
            return data;
        }
        if (deployment.getCheckingStatus() != null) {
            data.setState(deployment.getCheckingStatus() == 7
                    ? DataWorksOperationState.CHECK_FAILED : DataWorksOperationState.WAITING);
            return data;
        }
        if (deployment.getToEnvironment() != null && deployment.getToEnvironment() != 1) {
            throw new DataWorksApiException("NOT_A_SUBMISSION_PACKAGE",
                    "当前 deploymentId 不是开发环境提交包，不能据此再次发布", statusBody.getRequestId());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("NodeId", request.getNodeId());
        params.put("FileId", request.getFileId());
        params.put("ProjectId", properties.getProjectId());
        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("DeployFile", params);
        DeployFileResponse publishResponse = requireResponse(result, DeployFileResponse.class, "发布脚本");
        DeployFileResponseBody publishBody = publishResponse.getBody();
        requireBodySuccess(publishBody != null && Boolean.TRUE.equals(publishBody.getSuccess()), "发布脚本",
                publishBody == null ? null : publishBody.getErrorCode(),
                publishBody == null ? null : publishBody.getErrorMessage(),
                publishBody == null ? null : publishBody.getRequestId());
        data.setState(DataWorksOperationState.PUBLISH_ACCEPTED);
        data.setPublishDeploymentId(publishBody.getData());
        data.setPublishRequestId(publishBody.getRequestId());
        return data;
    }

    /**
     * 为周期调度节点创建补数据工作流。
     *
     * <p>DataWorks 的 RunCycleDagNodes 返回 dagId 列表，仅表示补数据请求已被受理，
     * 不表示节点已经执行成功。调用方后续应逐个使用 dagId 查询实例状态。</p>
     */
    public RunScriptResponse runScript(RunScriptRequest request) {
        validateDto(request);
        validateCycleBizDates(request.getStartBizDate(), request.getEndBizDate());
        validateCycleTimes(request.getBizBeginTime(), request.getBizEndTime());
        String projectEnv = resolveProjectEnv(request.getProjectEnv());

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("RootNodeId", request.getRootNodeId());
        params.put("StartBizDate", request.getStartBizDate());
        params.put("EndBizDate", request.getEndBizDate());
        params.put("Name", request.getName());
        params.put("ProjectEnv", projectEnv);
        params.put("Parallelism", request.getParallelism());
        params.put("IncludeNodeIds", request.getIncludeNodeIds());
        params.put("ExcludeNodeIds", request.getExcludeNodeIds());
        params.put("BizBeginTime", request.getBizBeginTime());
        params.put("BizEndTime", request.getBizEndTime());
        params.put("NodeParams", request.getNodeParams());
        params.put("RegionId", properties.getRegionId());

        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("RunCycleDagNodes", params);
        RunCycleDagNodesResponse response = requireResponse(
                result, RunCycleDagNodesResponse.class, "创建补数据工作流");
        RunCycleDagNodesResponseBody body = response.getBody();
        if (body == null || !Boolean.TRUE.equals(body.getSuccess())) {
            throw responseFailure("创建补数据工作流失败", body == null ? null : body.getErrorCode(),
                    body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        }
        if (body.getData() == null || body.getData().isEmpty()) {
            throw new DataWorksApiException("INVALID_UPSTREAM_RESPONSE",
                    "DataWorks 已返回成功，但 dagId 列表为空", body.getRequestId());
        }
        for (Long dagId : body.getData()) {
            if (dagId == null || dagId <= 0) {
                throw new DataWorksApiException("INVALID_UPSTREAM_RESPONSE",
                        "DataWorks 已返回成功，但 dagId 列表包含空值或非正数", body.getRequestId());
            }
        }

        RunScriptResponse data = new RunScriptResponse();
        data.setState(DataWorksOperationState.RUN_ACCEPTED);
        data.setDagIds(new ArrayList<Long>(body.getData()));
        data.setRootNodeId(request.getRootNodeId());
        data.setProjectEnv(projectEnv);
        data.setRequestId(body.getRequestId());
        return data;
    }

    /**
     * 按 runScript 返回的某个 dagId 查询本次补数据产生的实例状态。
     * 实例尚未生成时返回空列表；只有 DataWorks 调用失败时才抛异常。
     */
    public QueryExecutionResponse queryExecution(QueryExecutionRequest request) {
        validateDto(request);
        String projectEnv = resolveProjectEnv(request.getProjectEnv());

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("DagId", request.getDagId());
        params.put("ProjectId", properties.getProjectId());
        params.put("ProjectEnv", projectEnv);
        params.put("RegionId", properties.getRegionId());
        params.put("PageNumber", request.getPageNumber());
        params.put("PageSize", request.getPageSize());

        Map<String, Object> result = dataWorksSdkInvoker.executeFunction("ListInstances", params);
        ListInstancesResponse response = requireResponse(result, ListInstancesResponse.class, "查询执行状态");
        ListInstancesResponseBody body = response.getBody();
        if (body == null || !Boolean.TRUE.equals(body.getSuccess())) {
            throw responseFailure("查询执行状态失败", body == null ? null : body.getErrorCode(),
                    body == null ? null : body.getErrorMessage(), body == null ? null : body.getRequestId());
        }

        ListInstancesResponseBody.ListInstancesResponseBodyData source = body.getData();
        QueryExecutionResponse data = new QueryExecutionResponse();
        data.setDagId(request.getDagId());
        data.setProjectEnv(projectEnv);
        data.setPageNumber(request.getPageNumber());
        data.setPageSize(request.getPageSize());
        data.setRequestId(body.getRequestId());
        if (source == null) {
            data.setTotalCount(0);
            data.setHasMore(false);
            return data;
        }

        int totalCount = source.getTotalCount() == null ? 0 : source.getTotalCount();
        data.setTotalCount(totalCount);
        data.setHasMore((long) request.getPageNumber() * request.getPageSize() < totalCount);
        List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances = source.getInstances();
        if (instances == null || instances.isEmpty()) {
            return data;
        }
        for (ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instance : instances) {
            if (instance == null) {
                continue;
            }
            ExecutionInstanceResponse item = new ExecutionInstanceResponse();
            item.setInstanceId(instance.getInstanceId());
            item.setNodeId(instance.getNodeId());
            item.setNodeName(instance.getNodeName());
            item.setStatus(instance.getStatus());
            item.setBizdate(instance.getBizdate());
            item.setBeginRunningTime(instance.getBeginRunningTime());
            item.setFinishTime(instance.getFinishTime());
            item.setErrorMessage(instance.getErrorMessage());
            data.getInstances().add(item);
        }
        return data;
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
        ListNodesResponse firstResponse = requireResponse(listNodesResult, ListNodesResponse.class, "查询节点日志");
        ListNodesResponseBody firstBody = firstResponse.getBody();
        requireBodySuccess(firstBody != null && Boolean.TRUE.equals(firstBody.getSuccess()), "查询节点日志",
                firstBody == null ? null : firstBody.getErrorCode(),
                firstBody == null ? null : firstBody.getErrorMessage(),
                firstBody == null ? null : firstBody.getRequestId());
        ListNodesResponseBody.ListNodesResponseBodyData firstData = firstBody.getData();
        if (firstData == null || firstData.getTotalCount() == null || firstData.getTotalCount() == 0) {
            return;
        }
        int pageTotal = (firstData.getTotalCount() + pageSize - 1) / pageSize;
        for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
            params.put("PageNumber", folderPage);
            ListNodesResponse response = firstResponse;
            if (folderPage > 1) {
                listNodesResult = dataWorksSdkInvoker.executeFunction("ListNodes", params);
                response = requireResponse(listNodesResult, ListNodesResponse.class, "查询节点日志");
            }
            ListNodesResponseBody body = response.getBody();
            requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询节点日志",
                    body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                    body == null ? null : body.getRequestId());
            List<ListNodesResponseBody.ListNodesResponseBodyDataNodes> nodes =
                    body.getData() == null ? null : body.getData().getNodes();
            if (nodes == null) {
                continue;
            }
            for (ListNodesResponseBody.ListNodesResponseBodyDataNodes node : nodes) {
                if (node == null) {
                    continue;
                }
                log.info(node.getNodeName() + "--" + node.getNodeId());
                log.info(JSON.toJSONString(node));
                getListInstanceLog(node.getNodeId());
            }
        }
    }

    //GetInstance 调用GetInstance获取实例的详情。
    public void getInstance(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());
        Map<String, Object> getInstanceResult = dataWorksSdkInvoker.executeFunction("GetInstance", params);
        GetInstanceResponse response = requireResponse(getInstanceResult, GetInstanceResponse.class, "查询实例详情");
        GetInstanceResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询实例详情",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        GetInstanceResponseBody.GetInstanceResponseBodyData data = body.getData();
        if (data != null) {
            log.info("InstanceResponseBodyData: \n" + JSON.toJSONString(data));
        }
    }

    public QueryLogsResponse logs(QueryLogsRequest request) {
        validateDto(request);
        QueryLogsResponse data = new QueryLogsResponse();
        data.setInstanceId(request.getInstanceId());
        data.setNodeId(request.getNodeId());
        if (request.getInstanceId() != null) {
            data.setLogContent(getInstanceLog(request.getInstanceId()));
        } else {
            data.setLogs(getListInstanceLog(request.getNodeId()));
        }
        return data;
    }


    //    GetInstanceLog  调用GetInstanceLog获取实例的日志。
    public String getInstanceLog(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());
        Map<String, Object> getInstanceLogResult = dataWorksSdkInvoker.executeFunction("GetInstanceLog", params);
        GetInstanceLogResponse response = requireResponse(
                getInstanceLogResult, GetInstanceLogResponse.class, "查询实例日志");
        GetInstanceLogResponseBody body = response.getBody();
        requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询实例日志",
                body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                body == null ? null : body.getRequestId());
        return body.getData() == null ? "" : body.getData();
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
        ListInstancesResponse firstResponse = requireResponse(
                listInstancesResult, ListInstancesResponse.class, "查询节点实例");
        ListInstancesResponseBody firstBody = firstResponse.getBody();
        requireBodySuccess(firstBody != null && Boolean.TRUE.equals(firstBody.getSuccess()), "查询节点实例",
                firstBody == null ? null : firstBody.getErrorCode(),
                firstBody == null ? null : firstBody.getErrorMessage(),
                firstBody == null ? null : firstBody.getRequestId());
        ListInstancesResponseBody.ListInstancesResponseBodyData firstData = firstBody.getData();
        if (firstData == null || firstData.getTotalCount() == null || firstData.getTotalCount() == 0) {
            return logList;
        }
        int pageTotal = (firstData.getTotalCount() + pageSize - 1) / pageSize;
        for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
            params.put("PageNumber", folderPage);
            ListInstancesResponse response = firstResponse;
            if (folderPage > 1) {
                listInstancesResult = dataWorksSdkInvoker.executeFunction("ListInstances", params);
                response = requireResponse(listInstancesResult, ListInstancesResponse.class, "查询节点实例");
            }
            ListInstancesResponseBody body = response.getBody();
            requireBodySuccess(body != null && Boolean.TRUE.equals(body.getSuccess()), "查询节点实例",
                    body == null ? null : body.getErrorCode(), body == null ? null : body.getErrorMessage(),
                    body == null ? null : body.getRequestId());
            List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances =
                    body.getData() == null ? null : body.getData().getInstances();
            if (instances == null) {
                continue;
            }
            for (ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instanceDetail : instances) {
                if (instanceDetail == null) {
                    continue;
                }
                DwInstanceLogDetail logDetail = new DwInstanceLogDetail();
                logDetail.setBeginRunningTime(formatTime(instanceDetail.getBeginRunningTime(), "yyyy-MM-dd HH:mm:ss"))
                        .setBeginWaitResTime(formatTime(instanceDetail.getBeginWaitResTime(), "yyyy-MM-dd HH:mm:ss"))
                        .setBeginWaitTimeTime(formatTime(instanceDetail.getBeginWaitTimeTime(), "yyyy-MM-dd HH:mm:ss"))
                        .setBizdate(formatTime(instanceDetail.getBizdate(), "yyyyMMdd"))
                        .setCycTime(formatTime(instanceDetail.getCycTime(), "yyyy-MM-dd HH:mm:ss"))
                        .setDagId(instanceDetail.getDagId())
                        .setDqcType(instanceDetail.getDqcType())
                        .setFinishTime(formatTime(instanceDetail.getFinishTime(), "yyyy-MM-dd HH:mm:ss"))
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
        return logList;
    }



   /* public void updateNodeRunMode(Long nodeId, int schedulerType) {
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
    }*/

    private Map<String, Object> createFileParams(CreateScriptRequest request) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("RegionId", properties.getRegionId());
        params.put("FileName", request.getFileName());
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

    private String formatTime(Long timestamp, String pattern) {
        return timestamp == null ? "" : DateUtil.format(DateUtil.date(timestamp), pattern);
    }


    private void requireBodySuccess(boolean success, String operation, String errorCode,
                                    String message, String requestId) {
        if (!success) {
            throw responseFailure(operation + "失败", errorCode, message, requestId);
        }
    }

    private <T> T requireResponse(Map<String, Object> result, Class<T> type, String operation) {
        if (result == null || !Boolean.TRUE.equals(result.get("success"))) {
            throw invocationFailure(operation + "失败", result == null
                    ? Collections.<String, Object>emptyMap() : result);
        }
        return castResponse(result.get("response"), type, operation);
    }

    private DataWorksApiException responseFailure(String fallback, String errorCode,
                                                  String message, String requestId) {
        return new DataWorksApiException(defaultIfBlank(errorCode, "DATAWORKS_ERROR"),
                defaultIfBlank(message, fallback), requestId);
    }

    private DataWorksApiException invocationFailure(String fallback, Map<String, Object> result) {
        return new DataWorksApiException(defaultIfBlank(valueAsString(result.get("errCode")), "DATAWORKS_ERROR"),
                defaultIfBlank(valueAsString(result.get("errorMessage")), fallback),
                valueAsString(result.get("requestId")));
    }

    private String resolveProjectEnv(String projectEnv) {
        String value = projectEnv == null || projectEnv.trim().isEmpty()
                ? properties.getProjectEnv() : projectEnv;
        if (value == null || !("DEV".equalsIgnoreCase(value.trim()) || "PROD".equalsIgnoreCase(value.trim()))) {
            throw new IllegalArgumentException("projectEnv 必须为 DEV 或 PROD");
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private void validateCycleBizDates(String startBizDate, String endBizDate) {
        LocalDateTime start = parseCycleBizDate(startBizDate, "startBizDate");
        LocalDateTime end = parseCycleBizDate(endBizDate, "endBizDate");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endBizDate 不能早于 startBizDate");
        }
    }

    private LocalDateTime parseCycleBizDate(String value, String fieldName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(value, formatter);
            if (!formatter.format(dateTime).equals(value) || !LocalTime.MIDNIGHT.equals(dateTime.toLocalTime())) {
                throw new DateTimeParseException("格式不一致", value, 0);
            }
            return dateTime;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " 必须是有效日期，格式为 yyyy-MM-dd 00:00:00", e);
        }
    }

    private void validateCycleTimes(String bizBeginTime, String bizEndTime) {
        boolean beginEmpty = bizBeginTime == null || bizBeginTime.trim().isEmpty();
        boolean endEmpty = bizEndTime == null || bizEndTime.trim().isEmpty();
        if (beginEmpty != endEmpty) {
            throw new IllegalArgumentException("bizBeginTime 与 bizEndTime 必须同时传入");
        }
        if (!beginEmpty) {
            parseCycleTime(bizBeginTime, "bizBeginTime");
            parseCycleTime(bizEndTime, "bizEndTime");
        }
    }

    private void parseCycleTime(String value, String fieldName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        try {
            LocalTime time = LocalTime.parse(value, formatter);
            if (!formatter.format(time).equals(value)) {
                throw new DateTimeParseException("格式不一致", value, 0);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " 必须是有效时间，格式为 HH:mm:ss", e);
        }
    }

    private String valueAsString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private <T> T castResponse(Object response, Class<T> type, String operation) {
        if (!type.isInstance(response)) {
            throw new DataWorksApiException("INVALID_UPSTREAM_RESPONSE",
                    operation + "失败：DataWorks 返回类型异常", null);
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
