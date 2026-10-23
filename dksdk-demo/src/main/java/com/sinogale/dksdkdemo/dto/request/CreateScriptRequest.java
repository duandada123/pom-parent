package com.sinogale.dksdkdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;

@ApiModel(value = "CreateScriptRequest", description = "创建脚本请求")
@Getter
@Setter
public class CreateScriptRequest {
    @Positive
    @ApiModelProperty(value = "DataWorks 工作空间 ID；省略时使用 DATAWORKS_PROJECT_ID", example = "10000")
    private Long projectId;
    @NotBlank @Size(max = 255)
    @ApiModelProperty(value = "文件名称；日志查询时要求唯一精确匹配", example = "python_demo", required = true)
    private String fileName;
    @NotNull @Positive
    @ApiModelProperty(value = "文件代码类型，按工作空间支持类型填写；1221 为参考中的 Python 示例", example = "1221", required = true)
    private Integer fileType;
    @NotBlank
    @ApiModelProperty(value = "数据开发文件目录路径", example = "业务流程/demo/MaxCompute", required = true)
    private String fileFolderPath;
    @NotBlank
    @ApiModelProperty(value = "脚本代码内容", example = "print('hello DataWorks')", required = true)
    private String content;

    @ApiModelProperty(value = "文件描述", example = "SDK 示例脚本")
    private String fileDescription;

    @ApiModelProperty(value = "执行节点使用的数据源连接名称", example = "odps_source")
    private String connectionName;

    @ApiModelProperty(value = "节点上游输入依赖，按 DataWorks 格式填写")
    private String inputList;

    @ApiModelProperty(value = "兼容旧接口；20240902 SDK 的 CreateFile 不再提供 outputList 参数")
    private String outputList;

    @ApiModelProperty(value = "调度参数，多个参数用空格分隔", example = "bizdate=$bizdate")
    private String paraValue;

    @ApiModelProperty(value = "DataWorks 调度 cron 表达式", example = "00 30 05 * * ?")
    private String cronExpress;

    @ApiModelProperty(value = "调度周期类型", example = "DAY", allowableValues = "DAY,NOT_DAY")
    private String cycleType;

    @ApiModelProperty(value = "文件责任人的阿里云用户 UID")
    private String owner;

    @ApiModelProperty(value = "调度资源组标识符")
    private String resourceGroupIdentifier;
    @Positive
    @ApiModelProperty(value = "调度资源组 ID", example = "12345")
    private Long resourceGroupId;

    @ApiModelProperty(value = "兼容旧接口；20240902 SDK 的 CreateFile 不再提供自动建目录参数", example = "false")
    private Boolean createFolderIfNotExists = false;

    @ApiModelProperty(value = "是否由 DataWorks 自动解析代码依赖", example = "true")
    private Boolean autoParsing;
    @Min(0) @Max(10)
    @ApiModelProperty(value = "执行失败自动重跑次数，0～10", example = "0")
    private Integer autoRerunTimes;
    @Min(0) @Max(1800000)
    @ApiModelProperty(value = "失败重跑间隔，毫秒，0～1800000", example = "0")
    private Integer autoRerunIntervalMillis;

}
