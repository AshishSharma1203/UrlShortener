package com.example.analytics.consumer;

import java.time.Instant;

public record UrlAccessEvent(
        String alias,
        Instant accessedAt
) {}
