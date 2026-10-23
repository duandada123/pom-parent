package com.sinogale.dksdkdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinogale.dksdkdemo.config.DataWorksProperties;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Spring Boot live integration tests, modelled after the FeignTest style.
 *
 * <p>Cloud calls are opt-in. Add -Ddataworks.live-test.enabled=true for read calls and
 * -Ddataworks.live-test.allow-writes=true for create/submit/publish calls. IDs can be
 * supplied with the dataworks.live-test.* system properties used below.</p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = DksdkDemoApplication.class
)
@Slf4j
public class DksdkDemoApplicationTests {

    @Autowired
    private DataWorksController dataWorksController;

    @Autowired
    private DataWorksProperties dataWorksProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${dataworks.live-test.enabled:false}")
    private boolean liveTestEnabled;

    @Value("${dataworks.live-test.allow-writes:false}")
    private boolean liveWriteEnabled;

    @Value("${dataworks.live-test.file-id:0}")
    private long fileId;

    @Value("${dataworks.live-test.deployment-id:0}")
    private long deploymentId;

    @Value("${dataworks.live-test.instance-id:0}")
    private long instanceId;

    @Value("${dataworks.live-test.task-id:0}")
    private long taskId;

    @Value("${dataworks.live-test.bizdate:}")
    private String bizdate;

    @Value("${dataworks.live-test.file-type:1221}")
    private int fileType;

    @Value("${dataworks.live-test.folder-path:业务流程/dksdk-demo}")
    private String folderPath;

    @Test
    public void contextLoadsWithController() {
        assertNotNull(dataWorksController);
        assertNotNull(dataWorksProperties);
        assertNotNull(objectMapper);
    }

    @Test
    public void createScriptTest() throws Exception {
        assumeLiveWrite("创建脚本");
        assumeProjectConfigured();

        CreateScriptRequest request = new CreateScriptRequest();
        request.setProjectId(dataWorksProperties.getProjectId());
        request.setFileName("dksdk_live_test_" + System.currentTimeMillis());
        request.setFileType(fileType);
        request.setFileFolderPath(folderPath);
        request.setContent("print('hello DataWorks')");
        request.setFileDescription("dksdk-demo Spring Boot 集成测试");
        request.setCreateFolderIfNotExists(true);

        ApiResponse<CreateScriptResponse> response = dataWorksController.create(request);

        logResponse("createResult", response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertPositive("create fileId", response.getData().getFileId());
    }

    @Test
    public void submitScriptTest() throws Exception {
        assumeLiveWrite("提交脚本");
        assumeProjectConfigured();
        assumePositive("dataworks.live-test.file-id", fileId);

        SubmitScriptRequest request = new SubmitScriptRequest();
        request.setProjectId(dataWorksProperties.getProjectId());
        request.setFileId(fileId);
        request.setComment("dksdk-demo Spring Boot 集成测试提交");

        ApiResponse<SubmitScriptResponse> response = dataWorksController.submit(request);

        logResponse("submitResult", response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertPositive("submit deploymentId", response.getData().getDeploymentId());
    }

    @Test
    public void deploymentStatusTest() throws Exception {
        assumeLive("查询发布包状态");
        assumeProjectConfigured();
        assumePositive("dataworks.live-test.deployment-id", deploymentId);

        DeployScriptRequest request = new DeployScriptRequest();
        request.setProjectId(dataWorksProperties.getProjectId());
        request.setDeploymentId(deploymentId);
        request.setCheckOnly(true);

        ApiResponse<DeployScriptResponse> response = dataWorksController.deploy(request);

        logResponse("deploymentStatusResult", response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getState());
    }

    @Test
    public void publishScriptTest() throws Exception {
        assumeLiveWrite("发布脚本");
        assumeProjectConfigured();
        assumePositive("dataworks.live-test.file-id", fileId);
        assumePositive("dataworks.live-test.deployment-id", deploymentId);

        DeployScriptRequest request = new DeployScriptRequest();
        request.setProjectId(dataWorksProperties.getProjectId());
        request.setFileId(fileId);
        request.setDeploymentId(deploymentId);
        request.setComment("dksdk-demo Spring Boot 集成测试发布");

        ApiResponse<DeployScriptResponse> response = dataWorksController.deploy(request);

        logResponse("publishResult", response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getState());
    }

    @Test
    public void queryLogsTest() throws Exception {
        assumeLive("查询运行日志");

        QueryLogsRequest request = new QueryLogsRequest();
        if (instanceId > 0) {
            request.setInstanceId(instanceId);
        } else {
            assumeProjectConfigured();
            assumePositive("dataworks.live-test.task-id", taskId);
            Assume.assumeTrue("请设置 dataworks.live-test.bizdate=yyyy-MM-dd",
                    bizdate != null && !bizdate.trim().isEmpty());
            request.setProjectId(dataWorksProperties.getProjectId());
            request.setTaskId(taskId);
            request.setBizdate(LocalDate.parse(bizdate));
            request.setPageSize(20);
            request.setMaxPages(1);
        }

        ApiResponse<QueryLogsResponse> response = dataWorksController.logs(request);

        logResponse("queryLogsResult", response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getLogs());
    }

    private void assumeLive(String operation) {
        Assume.assumeTrue(operation + "需要设置 -Ddataworks.live-test.enabled=true", liveTestEnabled);
    }

    private void assumeLiveWrite(String operation) {
        assumeLive(operation);
        Assume.assumeTrue(operation + "会修改云端资源，需要额外设置 "
                + "-Ddataworks.live-test.allow-writes=true", liveWriteEnabled);
    }

    private void assumeProjectConfigured() {
        Assume.assumeTrue("请设置 DATAWORKS_PROJECT_ID 或 dataworks.project-id",
                dataWorksProperties.getProjectId() != null && dataWorksProperties.getProjectId() > 0);
    }

    private void assumePositive(String property, long value) {
        Assume.assumeTrue("请设置正数属性 " + property, value > 0);
    }

    private void assertPositive(String name, Long value) {
        assertTrue(name + " should be positive", value != null && value > 0);
    }

    private void logResponse(String name, Object response) throws Exception {
        log.info("{}: {}", name, objectMapper.writeValueAsString(response));
    }
}
