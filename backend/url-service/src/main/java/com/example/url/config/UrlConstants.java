package com.example.url.config;

import java.util.Set;

public final class UrlConstants {
    
    private UrlConstants() {
        // Private constructor to prevent instantiation
    }
    
    public static final int AUTO_ALIAS_LENGTH = 7;
    public static final int MAX_RETRIES = 5;
    
    public static final Set<String> RESERVED_ALIASES = Set.of(
        "auth", "url", "admin", "login", "signup"
    );

    public static final String BASE62="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    public static final int DEFAULT_EXPIRATION_DAYS = 365;
    public static final String URL_PREFIX = "http://localhost:8080/u/";
}
