package com.azki.order.dto;

import java.util.UUID;

public record OrderCreatedEvent (UUID orderId,
                                 String phoneNumber,
                                 String product) {}
