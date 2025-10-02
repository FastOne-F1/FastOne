package com.f1.fastone.order.dto;

import lombok.Builder;

@Builder
public class OrderItemDto {
    private String menuName;
    private int price;
    private int quantity;

}
