package com.example.url.service;

import com.example.url.dto.CreateUrlRequest;
import com.example.url.dto.CreateUrlResponse;
import com.example.url.model.Url;
import com.example.url.repository.UrlRepository;
import com.example.url.util.AliasGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.url.config.UrlConstants;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlService {


    private final UrlRepository urlRepository;

    public CreateUrlResponse createShortUrl(CreateUrlRequest request, UUID userId) {

        String alias;

        if (request.getCustomAlias() != null && !request.getCustomAlias().isBlank()) {
            alias = normalize(request.getCustomAlias());
            validateCustomAlias(alias);
        } else {
            alias = generateUniqueAlias();
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(UrlConstants.DEFAULT_EXPIRATION_DAYS, ChronoUnit.DAYS);

        Url url = Url.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .alias(alias)
                .originalUrl(request.getOriginalUrl())
                .createdAt(now)
                .expiresAt(expiresAt)
                .clickCount(0)
                .active(true)
                .build();

        urlRepository.save(url);

        return new CreateUrlResponse(
                UrlConstants.URL_PREFIX + alias,
                expiresAt
        );
    }

    private String generateUniqueAlias() {
        for (int i = 0; i < UrlConstants.MAX_RETRIES; i++) {
            String candidate = AliasGenerator.generate(UrlConstants.AUTO_ALIAS_LENGTH);
            if (!urlRepository.existsByAliasIgnoreCase(candidate)) {
                return candidate;
            }
        }
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to generate unique alias"
        );
    }

    private void validateCustomAlias(String alias) {
        if (UrlConstants.RESERVED_ALIASES.contains(alias)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Alias is reserved"
            );
        }

        if (urlRepository.existsByAliasIgnoreCase(alias)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Alias already exists"
            );
        }
    }

    private String normalize(String alias) {
        return alias.toLowerCase();
    }
}
