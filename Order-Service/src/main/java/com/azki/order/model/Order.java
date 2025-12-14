package com.azki.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Data
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String phoneNumber;
    private String product;
}