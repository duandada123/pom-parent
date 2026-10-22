package com.sinogale.dksdkdemo;

import com.sinogale.dksdkdemo.controller.DataWorksController;
import com.sinogale.dksdkdemo.dto.response.*;
import com.sinogale.dksdkdemo.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.sinogale.dksdkdemo.dto.request.QueryLogsRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DataWorksController.class)
public class DataWorksControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private DataWorksService service;
    @MockBean private DataWorksLogService logs;

    @Test public void createRejectsMissingContentBeforeSdkCall() throws Exception {
        mvc.perform(post("/api/dataworks/files/create").contentType("application/json")
                .content("{\"fileName\":\"x\",\"fileType\":1221,\"fileFolderPath\":\"a\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        verify(service, never()).create(any());
    }
    @Test public void invalidFileIdIsRejected() throws Exception {
        mvc.perform(post("/api/dataworks/files/submit").contentType("application/json")
                .content("{\"fileId\":-1}")).andExpect(status().isBadRequest());
        verify(service, never()).submit(any());
    }
    @Test public void checkOnlyDoesNotRequireFileId() throws Exception {
        when(service.deploy(any())).thenReturn(new DeployScriptResponse());
        mvc.perform(post("/api/dataworks/files/deploy").contentType("application/json")
                .content("{\"deploymentId\":20,\"checkOnly\":true}")).andExpect(status().isOk());
        verify(service).deploy(any());
    }
    @Test public void publishRequiresFileId() throws Exception {
        mvc.perform(post("/api/dataworks/files/deploy").contentType("application/json")
                .content("{\"deploymentId\":20}")).andExpect(status().isBadRequest());
        verify(service, never()).deploy(any());
    }
    @Test public void logsRequireSelectorAndDate() throws Exception {
        mvc.perform(post("/api/dataworks/logs/query").contentType("application/json")
                .content("{\"fileName\":\"demo\"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/dataworks/logs/query").contentType("application/json")
                .content("{\"bizdate\":\"2026-10-21\"}")).andExpect(status().isBadRequest());
        verify(logs, never()).query(any());
    }
    @Test public void logsRejectAmbiguousSelectorsAndInvalidDate() throws Exception {
        mvc.perform(post("/api/dataworks/logs/query").contentType("application/json")
                .content("{\"taskId\":1,\"instanceId\":2}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/dataworks/logs/query").contentType("application/json")
                .content("{\"taskId\":1,\"bizdate\":\"bad-date\"}")).andExpect(status().isBadRequest());
        verify(logs, never()).query(any());
    }
    @Test public void legacyNodeIdAliasMapsToSchedulingTaskId() throws Exception {
        when(logs.query(any())).thenReturn(new QueryLogsResponse());
        mvc.perform(post("/api/dataworks/logs/query").contentType("application/json")
                .content("{\"nodeId\":200,\"bizdate\":\"2026-10-21\",\"projectEnv\":\"PROD\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        ArgumentCaptor<QueryLogsRequest> request = ArgumentCaptor.forClass(QueryLogsRequest.class);
        verify(logs).query(request.capture());
        assertEquals(Long.valueOf(200), request.getValue().getTaskId());
    }
    @Test public void unknownFieldsAreNotSilentlyIgnored() throws Exception {
        mvc.perform(post("/api/dataworks/files/submit").contentType("application/json")
                .content("{\"fileId\":1,\"fileID\":2}")).andExpect(status().isBadRequest());
    }
}
