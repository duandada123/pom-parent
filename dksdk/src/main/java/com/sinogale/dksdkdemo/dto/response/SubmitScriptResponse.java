package com.sinogale.dksdkdemo.dto.response;

import lombok.Data;

@Data
public class SubmitScriptResponse {
    private String state;
    private Long deploymentId;
    private String requestId;
}
