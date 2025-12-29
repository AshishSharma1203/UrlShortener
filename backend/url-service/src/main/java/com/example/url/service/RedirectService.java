package com.example.url.service;

import com.example.url.model.Url;
import com.example.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static com.example.url.config.UrlConstants.DEFAULT_EXPIRATION_DAYS;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final UrlRepository urlRepository;

    public String resolve(String alias) {

        Url url = urlRepository.findActiveByAliasIgnoreCase(alias)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Short URL not found"
                ));

        if (url.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "Short URL has expired"
            );
        }

        // sync v1 updates
        url.setClickCount(url.getClickCount() + 1);
        url.setExpiresAt(
                Instant.now().plusSeconds(DEFAULT_EXPIRATION_DAYS * 24 * 60 * 60)
        );

        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}
