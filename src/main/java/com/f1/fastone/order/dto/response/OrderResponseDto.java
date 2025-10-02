package com.f1.fastone.order.dto.response;

import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {

    LocalDateTime createdDate;
    String storeName;
    List<OrderItemDto> orderItems;
    PaymentDto payment;

}
