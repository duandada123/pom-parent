package com.sinogale.dksdkdemo;

import com.alibaba.fastjson2.JSON;
import com.sinogale.dksdkdemo.dto.CreateScriptDto;
import com.sinogale.dksdkdemo.dto.DeployScriptDto;
import com.sinogale.dksdkdemo.dto.QueryLogsDto;
import com.sinogale.dksdkdemo.dto.SubmitScriptDto;
import com.sinogale.dksdkdemo.dto.response.ApiResponse;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import com.sinogale.dksdkdemo.service.DataWorksService;
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
 * <p>写法与 FeignTest 一致：启动 Spring 容器，注入 Service，直接构造 DTO 调用接口。
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
public class DataWorksServiceLiveTest {

    private static final boolean LIVE_TEST_ENABLED =
            Boolean.parseBoolean(System.getProperty("dataworks.live-test.enabled", "false"));
    private static final boolean LIVE_WRITE_ENABLED =
            Boolean.parseBoolean(System.getProperty("dataworks.live-test.allow-writes", "false"));
    private static final long FILE_ID = Long.getLong("dataworks.live-test.file-id", 0L);
    private static final long DEPLOYMENT_ID = Long.getLong("dataworks.live-test.deployment-id", 0L);
    private static final long INSTANCE_ID = Long.getLong("dataworks.live-test.instance-id", 0L);
    private static final long NODE_ID = Long.getLong("dataworks.live-test.node-id", 0L);

    @Autowired
    private DataWorksService dataWorksService;

    @Test
    public void createScriptTest() {
        assumeWriteEnabled("创建脚本");
        CreateScriptDto dto = new CreateScriptDto();
        dto.setFileName("dksdk_test_" + System.currentTimeMillis());
        dto.setFileType(1221);
        dto.setInputList("pro_ads_js_wsgwsjzttest_root");
       // dto.setFileFolderPath("业务流程/dctest");
        dto.setFileFolderPath("/业务流程/123/MaxCompute");
        dto.setContent("print('hello DataWorks')");
        dto.setFileDescription("dksdk Service 集成测试");
        dto.setCronExpress("00 00 01 * * ?");
        dto.setCycleType("DAY");
        dto.setAutoParsing(false);
        dto.setStop(false);
        ApiResponse<CreateScriptResponse> result = dataWorksService.create(dto);
        log.info("createScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertPositive("fileId", result.getData().getFileId());
    }

    @Test
    public void submitScriptTest() {
        assumeWriteEnabled("提交脚本");
        assumePositiveProperty("dataworks.live-test.file-id", FILE_ID);
        SubmitScriptDto dto = new SubmitScriptDto();
        dto.setFileId(FILE_ID);
        ApiResponse<SubmitScriptResponse> result = dataWorksService.submit(dto);
        log.info("submitScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertPositive("deploymentId", result.getData().getDeploymentId());
    }

    @Test
    public void deploymentStatusTest() {
        assumeLiveEnabled("查询提交或发布状态");
        assumePositiveProperty("dataworks.live-test.deployment-id", DEPLOYMENT_ID);
        DeployScriptDto dto = new DeployScriptDto();
        dto.setDeploymentId(DEPLOYMENT_ID);
        dto.setCheckOnly(true);
        ApiResponse<DeployScriptResponse> result = dataWorksService.deploy(dto);
        log.info("deploymentStatusResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getState());
    }

    @Test
    public void publishScriptTest() {
        assumeWriteEnabled("发布脚本");
        assumePositiveProperty("dataworks.live-test.file-id", FILE_ID);
        assumePositiveProperty("dataworks.live-test.deployment-id", DEPLOYMENT_ID);
        DeployScriptDto dto = new DeployScriptDto();
        dto.setDeploymentId(DEPLOYMENT_ID);
        dto.setFileId(FILE_ID);
        dto.setCheckOnly(false);
        ApiResponse<DeployScriptResponse> result = dataWorksService.deploy(dto);
        log.info("publishScriptResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getState());
    }

    @Test
    public void queryInstanceLogTest() {
        assumeLiveEnabled("按实例查询运行日志");
        assumePositiveProperty("dataworks.live-test.instance-id", INSTANCE_ID);
        QueryLogsDto dto = new QueryLogsDto();
        dto.setInstanceId(INSTANCE_ID);
        ApiResponse<QueryLogsResponse> result = dataWorksService.logs(dto);
        log.info("queryInstanceLogResult: {}", JSON.toJSONString(result));
        assertSuccess(result);
        assertNotNull(result.getData().getLogContent());
    }

    @Test
    public void queryNodeLogsTest() {
        assumeLiveEnabled("按节点查询昨日实例日志");
        assumePositiveProperty("dataworks.live-test.node-id", NODE_ID);
        QueryLogsDto dto = new QueryLogsDto();
        dto.setNodeId(NODE_ID);
        ApiResponse<QueryLogsResponse> result = dataWorksService.logs(dto);
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
