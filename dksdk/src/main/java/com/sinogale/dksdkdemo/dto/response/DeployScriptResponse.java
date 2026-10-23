package com.sinogale.dksdkdemo.dto.response;

import lombok.Data;

@Data
public class DeployScriptResponse {
    private String state;
    private Long deploymentId;
    private Long publishDeploymentId;
    private Integer status;
    private Integer checkingStatus;
    private Integer fromEnvironment;
    private Integer toEnvironment;
    private String errorMessage;
    private String statusRequestId;
    private String publishRequestId;
}
