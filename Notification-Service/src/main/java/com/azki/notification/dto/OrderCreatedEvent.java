package com.azki.notification.dto;

import lombok.Data;

@Data
public class OrderCreatedEvent {
    public Long orderId;
    public String phoneNumber;
    public String product;
}

