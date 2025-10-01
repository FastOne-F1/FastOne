package com.f1.fastone.order.dto.request;

import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
public class OrderCreateRequestDto {

    private String username;
    private UUID storeId;
    private UUID cartId;
    private String requestNote;

   private ShipToDto shipToDto;

   private PaymentDto paymentDto;

}
