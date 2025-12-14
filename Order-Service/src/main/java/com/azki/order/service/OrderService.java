package com.azki.order.service;

import com.azki.order.dto.OrderCreatedEvent;
import com.azki.order.model.Order;
import com.azki.order.model.OutboxEvent;
import com.azki.order.model.OutboxStatus;
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

        Order order = new Order();
        order.setPhoneNumber(phone);
        order.setProduct(product);
        orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.orderId = order.getId();
        event.phoneNumber = phone;
        event.product = product;

        OutboxEvent outbox = new OutboxEvent();
        outbox.setId(UUID.randomUUID());
        outbox.setEventType("ORDER_CREATED");
        outbox.setPayload(mapper.writeValueAsString(event));
        outbox.setStatus(OutboxStatus.NEW);

        outboxRepository.save(outbox);
    }
}
