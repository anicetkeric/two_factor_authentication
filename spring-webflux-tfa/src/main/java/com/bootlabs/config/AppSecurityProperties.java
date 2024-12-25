package com.bootlabs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties specific.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Data
@Component
@ConfigurationProperties(prefix = "application.security.jwt", ignoreUnknownFields = false)
public class AppSecurityProperties {

    private String secretKey;

    private long tokenValidity;

    private long tokenRefreshValidity;

    private long tokenTempValidity;
}