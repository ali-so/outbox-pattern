package com.azki.order.service;

import com.azki.order.model.OutboxEvent;
import com.azki.order.model.OutboxStatus;
import com.azki.order.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publish() {

        List<OutboxEvent> events = outboxRepository.findTop100ByStatusOrderByCreatedAt(OutboxStatus.NEW);

        for (OutboxEvent e : events) {
            try {
                kafkaTemplate.send(
                        "order-events",
                        e.getId().toString(),
                        e.getPayload()
                ).get();

                e.setStatus(OutboxStatus.SENT);
            } catch (Exception ignored) {}
        }
    }
}


