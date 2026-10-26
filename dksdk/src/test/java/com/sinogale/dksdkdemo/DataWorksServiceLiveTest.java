package com.sinogale.dksdkdemo;

import com.alibaba.fastjson2.JSON;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.dto.request.DeployScriptRequest;
import com.sinogale.dksdkdemo.dto.request.QueryExecutionRequest;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import com.sinogale.dksdkdemo.dto.request.RunScriptRequest;
import com.sinogale.dksdkdemo.dto.request.SubmitScriptRequest;
import com.sinogale.dksdkdemo.dto.response.CreateScriptResponse;
import com.sinogale.dksdkdemo.dto.response.DeployScriptResponse;
import com.sinogale.dksdkdemo.dto.response.QueryExecutionResponse;
import com.sinogale.dksdkdemo.dto.response.QueryLogsResponse;
import com.sinogale.dksdkdemo.dto.response.RunScriptResponse;
import com.sinogale.dksdkdemo.dto.response.SubmitScriptResponse;
import com.sinogale.dksdkdemo.enums.DataWorksOperationState;
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
import static org.junit.Assert.assertEquals;

/**
 * DataWorks 真实接口测试。
 *
 * <p>写法与 FeignTest 一致：启动 Spring 容器，注入 Service，直接构造 Request 调用接口。
 * 默认不会执行云端请求，运行时按需添加以下 JVM 参数：</p>
 *
 * <pre>
 * -Ddataworks.live-test.enabled=true
 * -Ddataworks.live-test.allow-writes=true
 * -Ddataworks.live-test.file-id=文件ID
 * -Ddataworks.live-test.deployment-id=发布包ID
 * -Ddataworks.live-test.instance-id=实例ID
 * -Ddataworks.live-test.node-id=节点ID
 * -Ddataworks.live-test.dag-id=执行任务返回的dagId
 * -Ddataworks.live-test.start-biz-date=yyyy-MM-dd 00:00:00
 * -Ddataworks.live-test.end-biz-date=yyyy-MM-dd 00:00:00
 * -Ddataworks.live-test.include-node-ids=节点ID1,节点ID2（可选，默认使用node-id）
 * -Ddataworks.live-test.project-env=DEV或PROD（可选）
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
    private static final long DAG_ID = Long.getLong("dataworks.live-test.dag-id", 0L);
    private static final String START_BIZ_DATE = System.getProperty("dataworks.live-test.start-biz-date", "");
    private static final String END_BIZ_DATE = System.getProperty("dataworks.live-test.end-biz-date", "");
    private static final String INCLUDE_NODE_IDS = System.getProperty("dataworks.live-test.include-node-ids", "");
    private static final String EXCLUDE_NODE_IDS = System.getProperty("dataworks.live-test.exclude-node-ids", "");
    private static final String PROJECT_ENV = System.getProperty("dataworks.live-test.project-env", "");
    private static final String NODE_PARAMS = System.getProperty("dataworks.live-test.node-params", "");
    private static final boolean PARALLELISM =
            Boolean.parseBoolean(System.getProperty("dataworks.live-test.parallelism", "false"));

    @Autowired
    private DataWorksService dataWorksService;

    @Test
    public void createScriptTest() {
        assumeWriteEnabled("创建脚本");
        CreateScriptRequest request = new CreateScriptRequest();
        request.setFileName("dksdk_test_" + System.currentTimeMillis());
        request.setFileType(1221);
        request.setInputList("pro_ads_js_wsgwsjzttest_root");
        request.setFileFolderPath("业务流程/123/MaxCompute");
        request.setContent("print('hello DataWorks')");
        request.setFileDescription("dksdk Service 集成测试");
        request.setCronExpress("00 00 01 * * ?");
        request.setCycleType("DAY");
        request.setAutoParsing(false);
        request.setStop(false);
        CreateScriptResponse result = dataWorksService.create(request);
        log.info("createScriptResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertPositive("fileId", result.getFileId());
    }

    @Test
    public void submitScriptTest() {
        assumeWriteEnabled("提交脚本");
        assumePositiveProperty("dataworks.live-test.file-id", FILE_ID);
        SubmitScriptRequest request = new SubmitScriptRequest();
        request.setFileId(FILE_ID);
        SubmitScriptResponse result = dataWorksService.submit(request);
        log.info("submitScriptResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertPositive("deploymentId", result.getDeploymentId());
    }

    @Test
    public void deploymentStatusTest() {
        assumeLiveEnabled("查询提交或发布状态");
        assumePositiveProperty("dataworks.live-test.deployment-id", DEPLOYMENT_ID);
        DeployScriptRequest request = new DeployScriptRequest();
        request.setDeploymentId(DEPLOYMENT_ID);
        request.setCheckOnly(true);
        DeployScriptResponse result = dataWorksService.deploy(request);
        log.info("deploymentStatusResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getState());
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
        DeployScriptResponse result = dataWorksService.deploy(request);
        log.info("publishScriptResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getState());
    }

    @Test
    public void runScriptTest() {
        assumeWriteEnabled("创建补数据工作流");
        assumePositiveProperty("dataworks.live-test.node-id", NODE_ID);
        assumeNotBlankProperty("dataworks.live-test.start-biz-date", START_BIZ_DATE);
        assumeNotBlankProperty("dataworks.live-test.end-biz-date", END_BIZ_DATE);

        RunScriptRequest request = new RunScriptRequest();
        request.setRootNodeId(NODE_ID);
        request.setStartBizDate(START_BIZ_DATE);
        request.setEndBizDate(END_BIZ_DATE);
        request.setName("dksdk_backfill_" + System.currentTimeMillis());
        request.setParallelism(PARALLELISM);
        request.setIncludeNodeIds(INCLUDE_NODE_IDS.trim().isEmpty()
                ? String.valueOf(NODE_ID) : INCLUDE_NODE_IDS);
        if (!PROJECT_ENV.trim().isEmpty()) {
            request.setProjectEnv(PROJECT_ENV);
        }
        if (!EXCLUDE_NODE_IDS.trim().isEmpty()) {
            request.setExcludeNodeIds(EXCLUDE_NODE_IDS);
        }
        if (!NODE_PARAMS.trim().isEmpty()) {
            request.setNodeParams(NODE_PARAMS);
        }

        RunScriptResponse result = dataWorksService.runScript(request);
        log.info("runScriptResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertEquals(DataWorksOperationState.RUN_ACCEPTED, result.getState());
        assertNotNull(result.getDagIds());
        assertTrue("dagIds 不能为空", !result.getDagIds().isEmpty());
        for (Long dagId : result.getDagIds()) {
            assertPositive("dagId", dagId);
        }
    }

    @Test
    public void queryExecutionTest() {
        assumeLiveEnabled("按dagId查询实例运行状态");
        assumePositiveProperty("dataworks.live-test.dag-id", DAG_ID);

        QueryExecutionRequest request = new QueryExecutionRequest();
        request.setDagId(DAG_ID);
        request.setPageNumber(1);
        request.setPageSize(20);
        if (!PROJECT_ENV.trim().isEmpty()) {
            request.setProjectEnv(PROJECT_ENV);
        }

        QueryExecutionResponse result = dataWorksService.queryExecution(request);
        log.info("queryExecutionResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getInstances());
        assertTrue("totalCount 不能为负数", result.getTotalCount() != null && result.getTotalCount() >= 0);
    }

    @Test
    public void queryInstanceLogTest() {
        assumeLiveEnabled("按实例查询运行日志");
        assumePositiveProperty("dataworks.live-test.instance-id", INSTANCE_ID);
        QueryLogsRequest request = new QueryLogsRequest();
        request.setInstanceId(INSTANCE_ID);
        QueryLogsResponse result = dataWorksService.logs(request);
        log.info("queryInstanceLogResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getLogContent());
    }

    @Test
    public void queryNodeLogsTest() {
        assumeLiveEnabled("按节点查询昨日实例日志");
        assumePositiveProperty("dataworks.live-test.node-id", NODE_ID);
        QueryLogsRequest request = new QueryLogsRequest();
        request.setNodeId(NODE_ID);
        QueryLogsResponse result = dataWorksService.logs(request);
        log.info("queryNodeLogsResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getLogs());
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

    private void assumeNotBlankProperty(String property, String value) {
        Assume.assumeTrue("请设置 JVM 参数 -D" + property + "=...",
                value != null && !value.trim().isEmpty());
    }

    private void assertPositive(String name, Long value) {
        assertTrue(name + " 必须为正数", value != null && value > 0);
    }
}
