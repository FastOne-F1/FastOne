package com.f1.fastone.order.dto;

import com.f1.fastone.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String menuName;
    private int price;
    private int quantity;


    static public OrderItemDto from(OrderItem orderItem) {
        return OrderItemDto.builder()
                .menuName(orderItem.getMenuName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .build();
    }

}

