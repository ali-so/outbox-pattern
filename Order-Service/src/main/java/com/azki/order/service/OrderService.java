package com.azki.order.service;

import com.azki.order.dto.OrderCreatedEvent;
import com.azki.order.model.*;
import com.azki.order.repository.OrderRepository;
import com.azki.order.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper mapper;

    @Transactional
    public void createOrder(String phone, String product) throws Exception {

        final var order = Order.builder()
                .phoneNumber(phone)
                .product(product)
                .build();
        orderRepository.save(order);

        final var event = new OrderCreatedEvent(order.getId(), phone, product);

        final var outbox = OutboxEvent.builder()
                .aggregateType(AggregateType.ORDER)
                .eventType(EventType.CREATE)
                .payload(mapper.writeValueAsString(event))
                .status(OutboxStatus.NEW)
                .build();

        outboxRepository.save(outbox);
    }
}
