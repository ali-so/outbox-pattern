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

        // Process NEW events first
        final var newEvents = outboxRepository.findTop100ByStatusOrderByCreatedAt(OutboxStatus.NEW);
        processEvents(newEvents);

        // Also retry FAILED events
        final var failedEvents = outboxRepository.findTop100ByStatusOrderByCreatedAt(OutboxStatus.PENDING);
        processEvents(failedEvents);
    }

    private void processEvents(List<OutboxEvent> events) {
        for (OutboxEvent e : events) {
            if (e.getRetryCount() > 50) {
                e.setStatus(OutboxStatus.FAILED);
                outboxRepository.save(e);
                return;
            }

            try {
                kafkaTemplate.send(
                        "order-events",
                        e.getId(),
                        e.getPayload()
                ).get();

                e.setStatus(OutboxStatus.SENT);
                e.setErrorMessage(null); // Clear error message on success
            } catch (Exception ex) {
                // Save error message and set status to FAILED
                e.setStatus(OutboxStatus.PENDING);
                final var errorMsg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
                // Truncate error message if too long (CLOB can handle large text, but we'll limit for readability)
                e.setErrorMessage(errorMsg.length() > 1000 ? errorMsg.substring(0, 1000) : errorMsg);
            } finally {
                e.setRetryCount(e.getRetryCount() + 1);
                outboxRepository.save(e);
            }
        }
    }
}


