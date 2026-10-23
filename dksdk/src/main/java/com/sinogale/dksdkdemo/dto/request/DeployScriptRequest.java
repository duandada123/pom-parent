package com.sinogale.dksdkdemo.dto.request;

import lombok.Data;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class DeployScriptRequest {
    @NotNull @Positive private Long deploymentId;
    @Positive private Long fileId;
    @Positive private Long nodeId;
    private boolean checkOnly;

    @AssertTrue(message = "发布时 fileId 和 nodeId 必须且只能填写一个")
    public boolean isSelectorValid() {
        return checkOnly || (fileId == null) != (nodeId == null);
    }
}
