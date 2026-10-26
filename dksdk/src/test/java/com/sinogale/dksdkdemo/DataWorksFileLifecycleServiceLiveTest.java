package com.sinogale.dksdkdemo;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dataworks_20240902.models.DeployFileResponse;
import com.sinogale.dksdkdemo.dto.request.CreateScriptRequest;
import com.sinogale.dksdkdemo.service.DataWorksFileLifecycleService;
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
 * 创建、提交、检查状态、发布的真实接口串联测试。
 * 默认跳过，必须显式开启写操作并提供目标目录和上游输出名称。
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = DksdkDemoApplication.class)
@Slf4j
public class DataWorksFileLifecycleServiceLiveTest {

    private static final boolean LIVE_TEST_ENABLED = Boolean.parseBoolean(
            System.getProperty("dataworks.lifecycle-test.enabled", "false"));
    private static final boolean LIVE_WRITE_ENABLED = Boolean.parseBoolean(
            System.getProperty("dataworks.lifecycle-test.allow-writes", "false"));
    private static final String FILE_FOLDER_PATH = System.getProperty(
            "dataworks.lifecycle-test.file-folder-path", "");
    private static final String INPUT_LIST = System.getProperty(
            "dataworks.lifecycle-test.input-list", "");
    @Autowired
    private DataWorksFileLifecycleService lifecycleService;

    @Test
    public void createSubmitCheckStatusAndPublishTest() {
        assumeWriteEnabled();
        Assume.assumeTrue("请设置 -Ddataworks.lifecycle-test.file-folder-path=...",
                FILE_FOLDER_PATH != null && !FILE_FOLDER_PATH.trim().isEmpty());
        Assume.assumeTrue("请设置 -Ddataworks.lifecycle-test.input-list=...",
                INPUT_LIST != null && !INPUT_LIST.trim().isEmpty());

        CreateScriptRequest createRequest = new CreateScriptRequest();
        createRequest.setFileName("dksdk_lifecycle_" + System.currentTimeMillis());
        createRequest.setFileType(1221);
        createRequest.setFileFolderPath(FILE_FOLDER_PATH);
        createRequest.setContent("print('hello DataWorks')");
        createRequest.setFileDescription("创建提交发布串联测试");
        createRequest.setInputList(INPUT_LIST);
        createRequest.setParaValue("bizdate=$bizdate");
        createRequest.setCronExpress("00 00 01 * * ?");
        createRequest.setCycleType("DAY");
        createRequest.setAutoRerunIntervalMillis(0);
        createRequest.setAutoRerunTimes(0);
        createRequest.setAutoParsing(false);
        createRequest.setStop(false);

        DeployFileResponse result = lifecycleService.createSubmitCheckAndPublish(createRequest);
        log.info("fileLifecycleResult: {}", JSON.toJSONString(result));
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertTrue("发布接口调用必须成功", Boolean.TRUE.equals(result.getBody().getSuccess()));
        assertPositive("publish deploymentId", result.getBody().getData());
    }

    private void assumeWriteEnabled() {
        Assume.assumeTrue("请设置 -Ddataworks.lifecycle-test.enabled=true", LIVE_TEST_ENABLED);
        Assume.assumeTrue("该测试会创建、提交和发布 DataWorks 文件，请设置 "
                + "-Ddataworks.lifecycle-test.allow-writes=true", LIVE_WRITE_ENABLED);
    }

    private void assertPositive(String name, Long value) {
        assertTrue(name + " 必须为正数", value != null && value > 0);
    }
}
