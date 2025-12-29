package com.example.url.controller;

import com.example.url.service.RedirectService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;

    @GetMapping("/u/{alias}")
    public void redirect(
            @PathVariable String alias,
            HttpServletResponse response
    ) throws IOException {

        String originalUrl = redirectService.resolve(alias);

        response.setStatus(HttpServletResponse.SC_FOUND); // 302
        response.setHeader("Location", originalUrl);
    }
}
