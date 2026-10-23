package com.sinogale.dksdkdemo.config;

import com.aliyun.dataworks_20240902.Client;
import com.aliyun.teaopenapi.models.Config;
import com.sinogale.dksdkdemo.exception.DataWorksException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** DataWorks 20240902 同步客户端的延迟初始化与复用。 */
@Component
public class DataWorksClientProvider {
    private final DataWorksProperties properties;
    private Client client;

    public DataWorksClientProvider(DataWorksProperties properties) {
        this.properties = properties;
    }

    public synchronized Client getClient() {
        if (client == null) {
            validateConfiguration();
            Config config = new Config()
                    .setAccessKeyId(properties.getAccessKeyId())
                    .setAccessKeySecret(properties.getAccessKeySecret())
                    .setRegionId(properties.getRegionId())
                    .setProtocol("HTTPS")
                    .setConnectTimeout(5000)
                    .setReadTimeout(properties.getCallTimeoutSeconds() * 1000);
            if (StringUtils.hasText(properties.getSecurityToken())) {
                config.setSecurityToken(properties.getSecurityToken());
            }
            if (StringUtils.hasText(properties.getEndpoint())) {
                config.setEndpoint(properties.getEndpoint());
            }
            try {
                client = new Client(config);
            } catch (Exception e) {
                throw new DataWorksException(503, "DATAWORKS_CLIENT_INIT_FAILED",
                        "DataWorks 客户端初始化失败，请检查地域、Endpoint 和凭据配置", null);
            }
        }
        return client;
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getAccessKeyId())
                || !StringUtils.hasText(properties.getAccessKeySecret())
                || !StringUtils.hasText(properties.getRegionId())) {
            throw new DataWorksException(503, "DATAWORKS_NOT_CONFIGURED",
                    "请配置 DataWorks region-id、access-key-id、access-key-secret", null);
        }
    }
}
