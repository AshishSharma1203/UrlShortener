package com.example.url.controller;

import com.example.url.dto.CreateUrlRequest;
import com.example.url.dto.CreateUrlResponse;
import com.example.url.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public CreateUrlResponse create(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateUrlRequest request
    ) {
        return urlService.createShortUrl(request, userId);
    }
}
