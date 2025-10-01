package com.f1.fastone.order.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public class OrderItemDto {
    private String menuName;
    private int price;
    private int quantity;
}
