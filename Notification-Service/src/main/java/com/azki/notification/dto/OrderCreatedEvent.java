package com.azki.notification.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderCreatedEvent {
    public UUID orderId;
    public String phoneNumber;
    public String product;
}

