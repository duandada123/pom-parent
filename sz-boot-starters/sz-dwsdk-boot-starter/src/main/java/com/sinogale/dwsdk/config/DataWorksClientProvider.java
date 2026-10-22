package com.sinogale.dwsdk.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dataworks_public20240518.AsyncClient;
import com.sinogale.dwsdk.exception.DataWorksException;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.policy.retry.RetryPolicy;
import org.springframework.util.StringUtils;
import java.time.Duration;

public class DataWorksClientProvider {
    private final DwSdkProperties properties;
    private AsyncClient client;

    public DataWorksClientProvider(DwSdkProperties properties) { this.properties = properties; }

    // 单例连接复用；延迟初始化，未配置凭据时仍可启动应用、执行本地测试。
    public synchronized AsyncClient getClient() {
        if (client == null) {
            if (!StringUtils.hasText(properties.getAccessKeyId())
                    || !StringUtils.hasText(properties.getAccessKeySecret())
                    || !StringUtils.hasText(properties.getRegionId())) {
                throw new DataWorksException(503, "DATAWORKS_NOT_CONFIGURED",
                        "请配置 dwsdk.region-id、dwsdk.access-key-id、dwsdk.access-key-secret", null);
            }
            Credential credential = Credential.builder().accessKeyId(properties.getAccessKeyId())
                    .accessKeySecret(properties.getAccessKeySecret())
                    .securityToken(StringUtils.hasText(properties.getSecurityToken())
                            ? properties.getSecurityToken() : null).build();
            ClientOverrideConfiguration config = ClientOverrideConfiguration.create()
                    .setConnectTimeout(Duration.ofSeconds(5))
                    .setResponseTimeout(Duration.ofSeconds(properties.getCallTimeoutSeconds()))
                    // 写接口无幂等令牌，超时后不自动重试，以免重复创建/发布。
                    .setRetryPolicy(RetryPolicy.none()).setProtocol("HTTPS");
            if (StringUtils.hasText(properties.getEndpoint())) {
                config.setEndpointOverride(properties.getEndpoint());
            }
            client = AsyncClient.builder().region(properties.getRegionId())
                    .credentialsProvider(StaticCredentialProvider.create(credential))
                    .overrideConfiguration(config).build();
        }
        return client;
    }

    public synchronized void close() {
        if (client != null) { client.close(); client = null; }
    }
}
