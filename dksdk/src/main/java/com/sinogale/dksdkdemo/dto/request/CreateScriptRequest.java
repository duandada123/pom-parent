package com.sinogale.dksdkdemo.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class CreateScriptRequest {
    @NotBlank private String fileName;
    @NotNull @Positive private Integer fileType;
    @NotBlank private String fileFolderPath;
    @NotBlank private String content;
    private String fileDescription;
    private String owner;
    private String connectionName;
    private String cronExpress;
    private String cycleType;
    private String dependentNodeIdList;
    private String dependentType;
    private String paraValue;
    private String inputList;
    private Long resourceGroupId;
    private String resourceGroupIdentifier;
    private String schedulerType;
    private Long startEffectDate;
    private Long endEffectDate;
    private Boolean autoParsing;
    private Integer autoRerunTimes;
    private Integer autoRerunIntervalMillis;
    private String rerunMode;
    private Boolean stop;
}
