package com.sinogale.dksdkdemo.service;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dataworks_20240902.Client;
import com.aliyun.dataworks_20240902.models.*;
import com.aliyun.tea.TeaModel;
import com.sinogale.dksdkdemo.config.ConfigKeys;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataWorksService {

    @Autowired
    private  Client client;

    @Autowired
    private DataWorksProperties properties;

    /**
     * 动态执行 DataWorks API
     * @param actionName API 动作名称
     * @param params 请求参数
     * @return 包含执行结果的 Map
     */
    public Map<String, Object> executeFunction(String actionName, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建请求类名（如：RunCycleDagNodesRequest）
            String requestClassName = ConfigKeys.REQUEST_PACKAGE  + actionName + "Request";
            // 通过反射创建请求对象
            Class<?> requestClass = Class.forName(requestClassName);
            Constructor<?> constructor = requestClass.getDeclaredConstructor();
            TeaModel request = (TeaModel) constructor.newInstance();
            // 设置请求参数
            processRequestParams(request, params);
            // 构建方法名（如：runCycleDagNodes）
            String methodName = actionName.substring(0, 1).toLowerCase() + actionName.substring(1);
            Method clientMethod = Client.class.getMethod(methodName,requestClass);
            Object response = clientMethod.invoke(client,request);
            result.put("success", true);
            result.put("response", response);
            result.put("requestId", extractRequestId(response));
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | 
                 IllegalAccessException | InvocationTargetException e) {
            result.put("success", false);
            assert e instanceof InvocationTargetException;
            result.put("errorMessage", "执行请求失败: " + ((InvocationTargetException) e).getTargetException().getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMessage", "未知异常: " + e.getMessage());
        }
        return result;
    }

    /**
     * 处理请求参数，包括 NodeParams 的特殊处理
     */
    private void processRequestParams(TeaModel request, Map<String, Object> params) 
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> requestClass = request.getClass();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            if(Objects.isNull(paramValue)) continue;
            // 特殊处理 NodeParams：将 Map 转换为 JSON 字符串
//            if ("NodeParams".equalsIgnoreCase(paramName) && paramValue instanceof Map) {
//                paramValue = JSON.toJSONString(paramValue);
//            }
            // 拼接 setter 方法名（如：setNodeParams）
            String methodName = "set" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
            // 查找并调用 setter 方法
            Method method = findSetterMethod(requestClass, methodName, paramValue);
            method.invoke(request, paramValue);
        }
    }

    /**
     * 查找匹配的 setter 方法
     */
    private Method findSetterMethod(Class<?> clazz, String methodName, Object value) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, value.getClass());
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    if (isAssignable(paramType, value.getClass())) {
                        return method;
                    }
                }
            }
            throw e;
        }
    }

    /**
     * 判断类型是否可赋值
     */
    private boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }
        // 处理基础类型与包装类的转换
        if (targetType == int.class && sourceType == Integer.class) return true;
        if (targetType == long.class && sourceType == Long.class) return true;
        if (targetType == double.class && sourceType == Double.class) return true;
        if (targetType == boolean.class && sourceType == Boolean.class) return true;
        return false;
    }

    /**
     * 从响应中提取 RequestId
     */
    private String extractRequestId(Object response) {
        try {
            Method method = response.getClass().getMethod("getRequestId");
            return (String) method.invoke(response);
        } catch (Exception e) {
            return "unknown";
        }
    }


    // 提交文件
    public SubmitFileResponse submitFile(Long fileId) {
        Map<String, Object> params = new HashMap<>();
       params.put("ProjectId", properties.getProjectId());
        params.put("FileId", fileId);
        Map<String, Object> result = executeFunction("SubmitFile", params);
        if ((Boolean) result.get("success")) {
            SubmitFileResponse response = (SubmitFileResponse)result.get("response");
            //发布包的ID，后续需要将该ID作为参数调用GetDeployment接口，获取本次发布的具体情况。
            return response;
        }else{
            System.err.println("提交文件失败(提交的FileId: " + fileId  +"_" + "): " + result.get("errMsg"));
            return null;
        }
    }

    public ListNodesResponseBody.ListNodesResponseBodyData getNodesInfo(String nodeName) {
        int pageSize = 10;
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectEnv",properties.getProjectEnv());
        params.put("ProjectId", properties.getProjectId());
//        params.put("Owner", "5229294322010124193");
        //PYODPS3 ODPS_SQL ODPSSPARK
        params.put("ProgramType", "PYODPS3");
        params.put("NodeName","python_证件号码联系方式档案错误_JCZX_000098");
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);

        Map<String, Object> result = executeFunction("ListNodes",params);

        if (!(Boolean) result.get("success")) {
            System.err.println("获取文件详情失败(FileId: " + nodeName + "): " + result.get("errMsg"));
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

        Map<String, Object> result = executeFunction("GetFile", params);
        if (!(Boolean) result.get("success")) {
            System.err.println("获取文件详情失败(FileId: " + fileId + "): " + result.get("errMsg"));
            return null;
        }

        return (GetFileResponse)result.get("response");
    }


    //用GetDeployment接口获取文件的提交状态
    public GetDeploymentResponse getDeployment(Long deploymentId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        params.put("DeploymentId", deploymentId);

        Map<String, Object> result = executeFunction("GetDeployment", params);
        if ((Boolean) result.get("success")) {
            GetDeploymentResponse response = (GetDeploymentResponse)result.get("response");
            return response;
        }else{
            System.err.println("getDeployment失败(提交的FileId: " + deploymentId  +"_" + "): " + result.get("errMsg"));
            return null;
        }
    }


    //用GetDeployment接口获取文件的提交状态
    public DeployFileResponse deployFile(Long fileId,Long nodeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectId", properties.getProjectId());
        if(null!=fileId){
            params.put("FileId", fileId);
        }
        if(null!=nodeId){
            params.put("NodeId", nodeId);
        }
        Map<String, Object> result = executeFunction("DeployFile", params);
        if ((Boolean) result.get("success")) {
            DeployFileResponse response = (DeployFileResponse)result.get("response");
            return response;
        }else{
            System.err.println("提交文件失败(发布的FileId: " + fileId  +"_" + "): " + result.get("errMsg"));
            return null;
        }
    }


    public void updateNodeRunMode(Long nodeId, int schedulerType) {
        Map<String, Object> updateNodeRunModeParams = new HashMap<>();
        updateNodeRunModeParams.put("NodeId", nodeId);
        updateNodeRunModeParams.put("ProjectEnv", properties.getProjectEnv());
        //0：表示解冻目标节点 。 2：表示冻结目标节点。
        updateNodeRunModeParams.put("SchedulerType", schedulerType);

        Map<String, Object> result = executeFunction("UpdateNodeRunMode", updateNodeRunModeParams);
        // 输出结果
        String type =  schedulerType==0?"解冻":"冻结";

        if ((Boolean) result.get("success")) {
            System.out.println(nodeId+"-"+type+"请求成功！");
            System.out.println("Response: " + JSON.toJSONString(result.get("response")));
        } else {
            System.err.println(nodeId+"-"+type+"请求失败！");
            System.err.println("Error: " + result.get("errorMessage"));
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
        Map<String, Object> ListFilesResult = executeFunction("ListFiles",listFilesParams);
        if ((boolean) ListFilesResult.get("success")) {
            ListFilesResponse response = (ListFilesResponse) ListFilesResult.get("response");
            Integer pageSize = response.getBody().getData().getPageSize();
            Integer totalCount = response.getBody().getData().getTotalCount();
            Integer pageNumber = response.getBody().getData().getPageNumber();
            System.out.println(pageSize+"---"+totalCount+"---"+pageNumber);
            List<ListFilesResponseBody.ListFilesResponseBodyDataFiles> files = response.getBody().getData().getFiles();
            for (int i = 0; i < files.size(); i++) {
                Map<String, Object> getFileParams = new HashMap<>();
                getFileParams.put("ProjectId", properties.getProjectId());
                getFileParams.put("FileId", files.get(i).getFileId());
                Map<String, Object> getFileResult = executeFunction("GetFile",getFileParams);
                if ((boolean) getFileResult.get("success")) {

                    GetFileResponse getFileresponse = (GetFileResponse) getFileResult.get("response");
                    GetFileResponseBody body = getFileresponse.getBody();
                    return body;
//                    Long nodeId = getFileresponse.getBody().getData().getFile().getNodeId();
//                    String fileName1 = getFileresponse.getBody().getData().getFile().getFileName();
//                    System.out.println(nodeId+"_"+fileName1);
                }
            }

        } else {
            System.err.println("CreateManualDag Failed, Error Code: " + ListFilesResult.get("errCode"));
            System.err.println("Error Message: " + ListFilesResult.get("errMsg"));
            System.err.println("Request ID: " + ListFilesResult.get("requestId"));
            return null;
        }

        return null;
    }


}