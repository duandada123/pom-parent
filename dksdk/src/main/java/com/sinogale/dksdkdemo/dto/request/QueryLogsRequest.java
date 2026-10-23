package com.sinogale.dksdkdemo.dto.request;

import lombok.Data;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Positive;

@Data
public class QueryLogsRequest {
    @Positive private Long instanceId;
    @Positive private Long nodeId;

    @AssertTrue(message = "instanceId 和 nodeId 必须且只能填写一个")
    public boolean isSelectorValid() {
        return (instanceId == null) != (nodeId == null);
    }
}
