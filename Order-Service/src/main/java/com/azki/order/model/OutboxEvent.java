package com.azki.order.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class OutboxEvent extends BaseEntity {

    @Id
    private UUID id;

    private String aggregateType;
    private Long aggregateId;
    private String eventType;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // NEW, SENT, PENDING

    private LocalDateTime createdAt;
}

