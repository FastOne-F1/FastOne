package com.f1.fastone.order.dto.response;

import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    UUID orderId;
    LocalDateTime createdDate;
    String storeName;
    List<OrderItemDto> orderItems;
    PaymentDto payment;
    OrderStatus orderStatus;


    static public OrderResponseDto from(Order order, PaymentDto paymentDto, List<OrderItemDto> orderItems) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .createdDate(order.getCreatedAt())
                .orderStatus(order.getStatus())
                .storeName(order.getStore().getName())
                .payment(paymentDto)
                .orderItems(orderItems)
                .build();
    }


}
