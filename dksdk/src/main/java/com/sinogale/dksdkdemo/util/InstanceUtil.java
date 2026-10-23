package com.sinogale.dksdkdemo.util;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.dataworks_20240902.models.*;
import com.sinogale.dksdkdemo.config.ConfigKeys;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
import com.sinogale.dksdkdemo.entity.DwInstanceLogDetail;
import com.sinogale.dksdkdemo.service.DataWorksService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class InstanceUtil {


    @Autowired
    private DataWorksService dataWorksV2Service;

    @Autowired
    private DataWorksProperties properties;

//    调用ListNodes获取节点的列表。
    public void getNodesLog(String nodeName) {
        int pageSize = 10;
        Map<String, Object> params = new HashMap<>();
        params.put("ProjectEnv", "PROD");
        params.put("ProjectId", ConfigKeys.PROJECTID);
        params.put("RegionId", ConfigKeys.REGIONID);
        params.put("ProgramType", "PYODPS3");
        params.put("NodeName", nodeName);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);


        Map<String, Object> listNodesResult = dataWorksV2Service.executeFunction("ListNodes", params);
        if ((boolean) listNodesResult.get("success")) {
            ListNodesResponse response = (ListNodesResponse) listNodesResult.get("response");

            ListNodesResponseBody.ListNodesResponseBodyData dataList = response.getBody().getData();

            Integer totalCount = dataList.getTotalCount();
            int pageTotal = (totalCount + pageSize - 1) / pageSize;
            for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
                params.put("PageNumber", folderPage);
                listNodesResult = dataWorksV2Service.executeFunction("ListNodes", params);
                if ((boolean) listNodesResult.get("success")) {
                    response = (ListNodesResponse) listNodesResult.get("response");
                    List<ListNodesResponseBody.ListNodesResponseBodyDataNodes> nodes = response.getBody().getData().getNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        ListNodesResponseBody.ListNodesResponseBodyDataNodes node = nodes.get(i);
                        System.out.println(node.getNodeName() + "--" + node.getNodeId());
                        System.out.println(JSON.toJSONString(node));
                        getListInstanceLog(node.getNodeId());
                    }
                }
            }
        } else {
            System.err.println("ListNodes Failed, Error Code: " + listNodesResult.get("errCode"));
            System.err.println("Error Message: " + listNodesResult.get("errMsg"));
            System.err.println("Request ID: " + listNodesResult.get("requestId"));
        }
    }


    //调用ListInstances获取实例的列表。
    public List<DwInstanceLogDetail> getListInstanceLog(Long nodeId) {

        String bizdate = DateUtil.yesterday().toString();
        int pageSize = 20;
        Map<String, Object> params = new HashMap<>();
        params.put("NodeId", nodeId);
        params.put("ProjectEnv", "PROD");
        params.put("ProjectId",properties.getProjectId() );
        //只查T-1的最新的业务时间的实例
        params.put("Bizdate", bizdate);
        params.put("PageNumber", 1);
        params.put("PageSize", pageSize);
        List<DwInstanceLogDetail> logList = new ArrayList<>();
        Map<String, Object> listInstancesResult = dataWorksV2Service.executeFunction("ListInstances", params);
        if ((boolean) listInstancesResult.get("success")) {
            ListInstancesResponse response = (ListInstancesResponse) listInstancesResult.get("response");

            ListInstancesResponseBody.ListInstancesResponseBodyData dataList = response.getBody().getData();
            Integer totalCount = dataList.getTotalCount();
            int pageTotal = (totalCount + pageSize - 1) / pageSize;
            DwInstanceLogDetail log = null;
            for (int folderPage = 1; folderPage <= pageTotal; folderPage++) {
                params.put("PageNumber", folderPage);

                listInstancesResult = dataWorksV2Service.executeFunction("ListInstances", params);
                if ((boolean) listInstancesResult.get("success")) {
                    response = (ListInstancesResponse) listInstancesResult.get("response");

                    List<ListInstancesResponseBody.ListInstancesResponseBodyDataInstances> instances = response.getBody().getData().getInstances();
                    for (int i = 0; i < instances.size(); i++) {
                        ListInstancesResponseBody.ListInstancesResponseBodyDataInstances instanceDetail = instances.get(i);
                        String beginRunningTime = null == instanceDetail.getBeginRunningTime() ? "" : DateUtil.format(DateUtil.date(instanceDetail.getBeginRunningTime()), "yyyy-MM-dd HH:mm:ss");
                        String finishTime = null == instanceDetail.getFinishTime() ? "" : DateUtil.format(DateUtil.date(instanceDetail.getFinishTime()), "yyyy-MM-dd HH:mm:ss");
                        log = new DwInstanceLogDetail();
                        log.setBeginRunningTime(beginRunningTime)
                                .setBeginWaitResTime(DateUtil.format(DateUtil.date(instanceDetail.getBeginWaitResTime()), "yyyy-MM-dd HH:mm:ss"))
                                .setBeginWaitTimeTime(DateUtil.format(DateUtil.date(instanceDetail.getBeginWaitTimeTime()), "yyyy-MM-dd HH:mm:ss"))
                                .setBizdate(DateUtil.format(DateUtil.date(instanceDetail.getBizdate()), "yyyyMMdd"))
                                .setCycTime(DateUtil.format(DateUtil.date(instanceDetail.getCycTime()), "yyyy-MM-dd HH:mm:ss"))
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
                        log.setLogContent(instanceLog);
                        System.out.println(JSON.toJSONString(log));
                        logList.add(log);

                    }

                }
            }
        } else {
            System.err.println("getListInstanceLog Failed, Error Code: " + listInstancesResult.get("errCode"));
            System.err.println("Error Message: " + listInstancesResult.get("errMsg"));
            System.err.println("Request ID: " + listInstancesResult.get("requestId"));
        }

        return logList;
    }


    //GetInstance 调用GetInstance获取实例的详情。
    public void getInstance(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());

        Map<String, Object> getInstanceResult = dataWorksV2Service.executeFunction("GetInstance", params);
        if ((boolean) getInstanceResult.get("success")) {
            GetInstanceResponse response = (GetInstanceResponse) getInstanceResult.get("response");
            GetInstanceResponseBody.GetInstanceResponseBodyData data = response.getBody().getData();
            System.out.println("InstanceResponseBodyData: \n" + JSON.toJSONString(data));

        } else {
            System.err.println("CreateManualDag Failed, Error Code: " + getInstanceResult.get("errCode"));
            System.err.println("Error Message: " + getInstanceResult.get("errMsg"));
            System.err.println("Request ID: " + getInstanceResult.get("requestId"));
        }
    }

    //    GetInstanceLog  调用GetInstanceLog获取实例的日志。
    public String getInstanceLog(Long instanceId) {
        Map<String, Object> params = new HashMap<>();
        params.put("InstanceId", instanceId);
        params.put("ProjectEnv", properties.getProjectEnv());
        String message = "";

        Map<String, Object> getInstanceLogResult = dataWorksV2Service.executeFunction("GetInstanceLog", params);
        if ((boolean) getInstanceLogResult.get("success")) {
            GetInstanceLogResponse response = (GetInstanceLogResponse) getInstanceLogResult.get("response");
            message = response.getBody().getData();
        } else {
            System.err.println("getInstanceLog Failed, Error Code: " + getInstanceLogResult.get("errCode"));
            System.err.println("Error Message: " + getInstanceLogResult.get("errMsg"));
            System.err.println("Request ID: " + getInstanceLogResult.get("requestId"));
        }
        return message;
    }


}
