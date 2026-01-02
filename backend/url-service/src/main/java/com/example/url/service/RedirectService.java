package com.example.url.service;

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

    public String resolve(String alias) {
        System.out.println("alias received is "+alias);

        String normalizedAlias = alias.toLowerCase();
        String redisKey = REDIS_REDIRECT_KEY_PREFIX + normalizedAlias;

        // 1️⃣ Redis HIT
        String cachedUrl = redisTemplate.opsForValue().get(redisKey);
        System.out.println("is cached found "+cachedUrl);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        // 2️⃣ Redis MISS → DB
        Url url = urlRepository.findActiveByAliasIgnoreCase(normalizedAlias)
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

        // 3️⃣ Cache in Redis
        Duration ttl = Duration.between(Instant.now(), url.getExpiresAt());
        redisTemplate.opsForValue()
                .set(redisKey, url.getOriginalUrl(), ttl);

        // 4️⃣ SYNC v2 still updates DB (we remove this in Kafka step)
        url.setClickCount(url.getClickCount() + 1);
        url.setExpiresAt(
                Instant.now().plusSeconds(DEFAULT_EXPIRATION_DAYS * 24 * 60 * 60)
        );
        urlRepository.save(url);

        return url.getOriginalUrl();
    }
}
