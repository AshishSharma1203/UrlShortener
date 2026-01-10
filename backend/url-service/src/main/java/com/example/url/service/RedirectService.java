package com.example.url.service;

import com.example.url.analytics.AnalyticsProducer;
import com.example.url.model.Url;
import com.example.url.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;

import static com.example.url.config.UrlConstants.*;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final AnalyticsProducer analyticsProducer;

    public String resolve(String alias) {

        String normalizedAlias = alias.toLowerCase();
        String redisKey = REDIS_REDIRECT_KEY_PREFIX + normalizedAlias;

        // 1️⃣ Redis HIT
        String cachedUrl = redisTemplate.opsForValue().get(redisKey);
        if (cachedUrl != null) {
            analyticsProducer.publishUrlAccess(normalizedAlias);
            return cachedUrl;
        }

        // 2️⃣ Redis MISS → DB
        Url url = urlRepository.findActiveByAliasIgnoreCase(normalizedAlias)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Short URL not found"
                ));

        if (url.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(
                    HttpStatus.GONE, "Short URL has expired"
            );
        }

        // 3️⃣ Cache
        Duration ttl = Duration.between(Instant.now(), url.getExpiresAt());
        redisTemplate.opsForValue()
                .set(redisKey, url.getOriginalUrl(), ttl);

        // 4️⃣ Kafka event (ASYNC)
        analyticsProducer.publishUrlAccess(normalizedAlias);

        return url.getOriginalUrl();
    }
}