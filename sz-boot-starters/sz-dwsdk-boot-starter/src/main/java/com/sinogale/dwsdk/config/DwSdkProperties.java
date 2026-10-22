package com.sinogale.dwsdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ConfigurationProperties(prefix = "dwsdk")
@Validated
@Getter
@Setter
public class DwSdkProperties {

    /**
     * 是否启用 DW SDK 自动配置，默认开启，与 Redisson starter 一致。
     */
    private boolean enabled = true;

    /** 自定义服务主机名，不含协议及路径；为空时由 SDK 解析。 */
    private String endpoint = "";
    /** DataWorks 区域。 */
    private String regionId = "cn-hangzhou";
    /** 默认工作空间 ID，请求中可覆盖。 */
    private Long projectId;
    /** AccessKey ID，建议通过环境变量注入。 */
    private String accessKeyId = "";
    /** AccessKey Secret，建议通过环境变量注入。 */
    private String accessKeySecret = "";
    /** 临时凭据的 STS Token。 */
    private String securityToken = "";
    /** 业务日期转毫秒时间戳使用的时区。 */
    private String bizdateZone = "Asia/Shanghai";
    /** 单次 SDK 调用等待上限，单位秒。 */
    @Min(1) @Max(120)
    private int callTimeoutSeconds = 20;
    /** 按名称查节点的最大页数。 */
    @Min(1) @Max(100)
    private int maxNodePages = 20;
}
