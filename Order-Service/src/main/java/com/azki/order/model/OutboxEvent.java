package com.azki.order.model;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "OUTBOX_EVENTS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent extends BaseEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(columnDefinition = "CLOB")
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    @Column(columnDefinition = "CLOB")
    private String errorMessage;

    private int retryCount = 0;

    @PrePersist
    public void generateId() {
        if (id == null) {
            TimeBasedGenerator generator = Generators.timeBasedGenerator();
            this.id = generator.generate().toString() + "$$" + this.aggregateType + "$$" + this.eventType;
        }
    }
}

