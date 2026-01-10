package com.example.analytics.consumer;

import com.example.analytics.model.UrlAnalytics;
import com.example.analytics.repository.UrlAnalyticsRepository;
import com.example.analytics.consumer.UrlAccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlAccessConsumer {

    private final UrlAnalyticsRepository repository;

    @KafkaListener(
            topics = "url-access-events",
            groupId = "url-analytics-group-v5"
    )
    public void consume(UrlAccessEvent event) {
        log.info("Received event: {}", event);
        repository.findById(event.alias())
                .map(existing -> {
                    existing.setClickCount(existing.getClickCount() + 1);
                    return existing;
                })
                .orElseGet(() ->
                        UrlAnalytics.builder()
                                .alias(event.alias())
                                .clickCount(1)
                                .build()
                );

        repository.save(
                repository.findById(event.alias())
                        .orElseThrow()
        );
    }
}
