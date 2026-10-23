package com.sinogale.dksdkdemo.dto.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class SubmitScriptRequest {
    @NotNull @Positive private Long fileId;
}
