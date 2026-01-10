package com.example.url.analytics;

import java.time.Instant;

public record UrlAccessEvent(
        String alias,
        Instant accessedAt
) {}
