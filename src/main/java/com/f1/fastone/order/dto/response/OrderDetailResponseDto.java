package com.f1.fastone.order.dto.response;

import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDetailResponseDto {
    private UUID orderId;
    private String orderStatus;
    private String storeName;
    private LocalDateTime createdAt;
    private ShipToDto shipToDto;
    private PaymentDto paymentDto;
    private String requestNote;
    private List<OrderItemDto> orderItemDtos;
}
