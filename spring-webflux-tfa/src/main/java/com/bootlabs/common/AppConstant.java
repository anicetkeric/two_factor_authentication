package com.bootlabs.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstant {
    public static final String CODE_KEY = "code";
    public final String TOKEN_PREFIX = "Bearer ";
    public static final String MESSAGE_KEY = "message";
    public final String AUTHORITIES_KEY = "auth";
    public final String DEFAULT_SRC_SELF_POLICY = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";
    public final String PERMISSION_POLICY = "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()";

    // 24h Hours to Milliseconds = 86400000 ms
    public final long TOKEN_VALIDITY_TIME = 1_000L*86400;

    // temp token validity = 10min
    public final long TOKEN_TEMP_VALIDITY_TIME = 1000L*600;

}
