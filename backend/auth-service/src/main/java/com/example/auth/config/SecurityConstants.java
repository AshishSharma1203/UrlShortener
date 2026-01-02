package com.example.auth.config;

import java.time.temporal.ChronoUnit;

public final class SecurityConstants {
    
    // JWT Configuration
    public static final String JWT_SECRET = "${jwt.secret}";
    public static final long JWT_ACCESS_TOKEN_EXPIRATION_MINUTES = 10;
    public static final long REFRESH_TOKEN_EXPIRATION_DAYS = 30;
    
    // Token Prefix and Header
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    
    // Public endpoints
    public static final String[] PUBLIC_ENDPOINTS = {
        "/auth/**",
        "/u/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/actuator/**"
    };
    
    // Token generation
    public static final int REFRESH_TOKEN_BYTES = 64;
    public static final String HASH_ALGORITHM = "SHA-256";
    
    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }
}
