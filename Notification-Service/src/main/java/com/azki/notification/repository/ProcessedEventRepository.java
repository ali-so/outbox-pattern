package com.azki.notification.repository;

import com.azki.notification.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    // Optional explicit method for readability
    boolean existsByEventId(UUID eventId);
}

