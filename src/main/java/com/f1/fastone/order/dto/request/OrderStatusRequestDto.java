package com.f1.fastone.order.dto.request;

import com.f1.fastone.order.entity.OrderStatus;
import lombok.Getter;

@Getter
public class OrderStatusRequestDto {
    OrderStatus orderStatus;
}
