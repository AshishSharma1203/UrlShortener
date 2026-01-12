package com.example.analytics.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {

        // retry 3 times, 1 second apart
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        DefaultErrorHandler handler =
                new DefaultErrorHandler(
                        new DeadLetterPublishingRecoverer(
                                kafkaTemplate,
                                (record, ex) -> new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                        ),
                        backOff
                );

        // Do NOT retry deserialization issues
        handler.addNotRetryableExceptions(
                DeserializationException.class
        );

        return handler;
    }
}
