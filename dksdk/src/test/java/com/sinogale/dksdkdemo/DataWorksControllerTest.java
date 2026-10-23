package com.sinogale.dksdkdemo;

import com.alibaba.fastjson2.JSON;
import com.sinogale.dksdkdemo.controller.DataWorksController;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.ApiResponse;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * DataWorks 真实接口测试。
 *
 * <p>写法与 FeignTest 一致：启动 Spring 容器，注入 Controller，直接构造 DTO 调用接口。
 * 默认不会执行云端请求，运行时按需添加以下 JVM 参数：</p>
 *
 * <pre>
 * -Ddataworks.live-test.enabled=true
 * -Ddataworks.live-test.allow-writes=true
 * -Ddataworks.live-test.file-id=文件ID
 * -Ddataworks.live-test.deployment-id=发布包ID
 * -Ddataworks.live-test.instance-id=实例ID
 * -Ddataworks.live-test.node-id=节点ID
 * </pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = DksdkDemoApplication.class
)
@Slf4j
public class DataWorksControllerTest {

    private static final boolean LIVE_TEST_ENABLED =
            Boolean.parseBoolean(System.getProperty("dataworks.live-test.enabled", "true"));
    private static final boolean LIVE_WRITE_ENABLED =
            Boolean.parseBoolean(System.getProperty("dataworks.live-test.allow-writes", "true"));

    private static final long FILE_ID = Long.getLong("dataworks.live-test.file-id", 0L);
    private static final long DEPLOYMENT_ID = Long.getLong("dataworks.live-test.deployment-id", 0L);
    private static final long INSTANCE_ID = Long.getLong("dataworks.live-test.instance-id", 0L);
    private static final long NODE_ID = Long.getLong("dataworks.live-test.node-id", 0L);

    @Autowired
    private DataWorksController dataWorksController;

    @Test
    public void createScriptTest() {
        assumeWriteEnabled("创建脚本");
        CreateScriptRequest request = new CreateScriptRequest();
        request.setFileName("dksdk_test_" + System.currentTimeMillis());
        request.setFileType(1221);
        request.setInputList("pro_ads_js_wsgwsjzttest_root");
       // request.setFileFolderPath("业务流程/dctest");
        request.setFileFolderPath("/业务流程/123/MaxCompute");
        request.setContent("print('hello DataWorks')");
        request.setFileDescription("dksdk Controller 集成测试");
        request.setCronExpress("00 00 01 * * ?");
        request.setCycleType("DAY");
        request.setAutoParsing(false);
        request.setStop(false);

        ApiResponse<CreateScriptResponse> result = dataWorksController.create(request);

        log.info("createScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertPositive("fileId", result.getData().getFileId());
    }

    @Test
    public void submitScriptTest() {
        assumeWriteEnabled("提交脚本");
        assumePositiveProperty("dataworks.live-test.file-id", FILE_ID);
        SubmitScriptRequest request = new SubmitScriptRequest();
        request.setFileId(FILE_ID);
        ApiResponse<SubmitScriptResponse> result = dataWorksController.submit(request);
        log.info("submitScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertPositive("deploymentId", result.getData().getDeploymentId());
    }

    @Test
    public void deploymentStatusTest() {
        assumeLiveEnabled("查询提交或发布状态");
        assumePositiveProperty("dataworks.live-test.deployment-id", DEPLOYMENT_ID);
        DeployScriptRequest request = new DeployScriptRequest();
        request.setDeploymentId(DEPLOYMENT_ID);
        request.setCheckOnly(true);

        ApiResponse<DeployScriptResponse> result = dataWorksController.deploy(request);

        log.info("deploymentStatusResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getState());
    }

    @Test
    public void publishScriptTest() {
        assumeWriteEnabled("发布脚本");
        assumePositiveProperty("dataworks.live-test.file-id", FILE_ID);
        assumePositiveProperty("dataworks.live-test.deployment-id", DEPLOYMENT_ID);

        DeployScriptRequest request = new DeployScriptRequest();
        request.setDeploymentId(DEPLOYMENT_ID);
        request.setFileId(FILE_ID);
        request.setCheckOnly(false);

        ApiResponse<DeployScriptResponse> result = dataWorksController.deploy(request);

        log.info("publishScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getState());
    }

    @Test
    public void queryInstanceLogTest() {
        assumeLiveEnabled("按实例查询运行日志");
        assumePositiveProperty("dataworks.live-test.instance-id", INSTANCE_ID);

        QueryLogsRequest request = new QueryLogsRequest();
        request.setInstanceId(INSTANCE_ID);

        ApiResponse<QueryLogsResponse> result = dataWorksController.logs(request);

        log.info("queryInstanceLogResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getLogContent());
    }

    @Test
    public void queryNodeLogsTest() {
        assumeLiveEnabled("按节点查询昨日实例日志");
        assumePositiveProperty("dataworks.live-test.node-id", NODE_ID);

        QueryLogsRequest request = new QueryLogsRequest();
        request.setNodeId(NODE_ID);

        ApiResponse<QueryLogsResponse> result = dataWorksController.logs(request);

        log.info("queryNodeLogsResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getLogs());
    }

    private void assumeLiveEnabled(String operation) {
        Assume.assumeTrue(operation + "需要设置 -Ddataworks.live-test.enabled=true", LIVE_TEST_ENABLED);
    }

    private void assumeWriteEnabled(String operation) {
        assumeLiveEnabled(operation);
        Assume.assumeTrue(operation + "会修改 DataWorks 云端资源，需要设置 "
                + "-Ddataworks.live-test.allow-writes=true", LIVE_WRITE_ENABLED);
    }

    private void assumePositiveProperty(String property, long value) {
        Assume.assumeTrue("请设置正数 JVM 参数 -D" + property + "=...", value > 0);
    }

    private void assertSuccess(ApiResponse<?> response) {
        assertNotNull(response);
        assertTrue(response.getMessage(), response.isSuccess());
        assertNotNull(response.getData());
    }

    private void assertPositive(String name, Long value) {
        assertTrue(name + " 必须为正数", value != null && value > 0);
    }
}
