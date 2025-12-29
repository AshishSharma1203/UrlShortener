package com.example.url.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class CreateUrlResponse {
    private String shortUrl;
    private Instant expiresAt;
}
