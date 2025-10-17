package com.f1.fastone.order.dto.response;

import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponseDto {
    private UUID orderId;
    private OrderStatus orderStatus;
    private String storeName;
    private LocalDateTime createdDate;
    private ShipToDto shipTo;
    private PaymentDto payment;
    private String requestNote;
    private List<OrderItemDto> orderItems;

    static public OrderDetailResponseDto from(Order order, ShipToDto shipToDto, PaymentDto paymentDto, List<OrderItemDto> orderItemDtos) {
        return OrderDetailResponseDto.builder()
                .orderId(order.getId())
                .storeName(order.getStore().getName())
                .createdDate(order.getCreatedAt())
                .shipTo(shipToDto)
                .payment(paymentDto)
                .requestNote(order.getRequestNote())
                .orderItems(orderItemDtos)
                .orderStatus(order.getStatus())
                .build();
    }
}
