package com.example.url.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.example.url.config.UrlConstants.KAFKA_URL_ACCESS_TOPIC;

@Component
@RequiredArgsConstructor
public class AnalyticsProducer {

    private final KafkaTemplate<String, UrlAccessEvent> kafkaTemplate;

    public void publishUrlAccess(String alias) {
        UrlAccessEvent event =
                new UrlAccessEvent(alias, java.time.Instant.now());
        System.out.println("publsihing event "+ event);
        kafkaTemplate.send(
                KAFKA_URL_ACCESS_TOPIC,
                alias,      // key (helps partitioning later)
                event
        );
    }
}
