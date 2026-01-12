package com.example.analytics.consumer;

import com.example.analytics.model.UrlAnalytics;
import com.example.analytics.repository.UrlAnalyticsRepository;
import com.example.analytics.consumer.UrlAccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlAccessConsumer {

    private final UrlAnalyticsRepository repository;

    @KafkaListener(
            topics = "url-access-events",
            groupId = "url-analytics-group"
    )
    @Transactional
    public void consume(UrlAccessEvent event) {

        log.info("Consumed event: {}", event);
        // 🔥 FORCE FAILURE
        if (event.alias().equals("fail-test")) {
            log.error("Simulated failure for alias=fail-test");
            throw new RuntimeException("Simulated failure");
        }
        int updated = repository.incrementClick(event.alias());

        if (updated == 0) {
            repository.save(
                    UrlAnalytics.builder()
                            .alias(event.alias())
                            .clickCount(1)
                            .build()
            );
        }

    }
}
