package com.azki.notification.service;

import com.azki.notification.dto.OrderCreatedEvent;
import com.azki.notification.model.ProcessedEvent;
import com.azki.notification.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ProcessedEventRepository repository;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "order-events")
    @Transactional
    public void consume(
            String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String eventId,
            Acknowledgment ack) throws Exception {

        UUID id = UUID.fromString(eventId);

        if (repository.existsById(id)) {
            ack.acknowledge();
            return;
        }

        OrderCreatedEvent event =
                mapper.readValue(payload, OrderCreatedEvent.class);

        // Simulate SMS
        System.out.println(
                "📩 SMS to " + event.phoneNumber +
                        ": Your order for " + event.product + " is confirmed!"
        );

        repository.save(
                new ProcessedEvent(id, LocalDateTime.now())
        );

        ack.acknowledge();
    }
}


