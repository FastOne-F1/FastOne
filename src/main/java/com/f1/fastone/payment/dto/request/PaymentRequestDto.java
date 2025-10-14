package com.f1.fastone.payment.dto.request;

import java.util.UUID;

public record PaymentRequestDto(
        UUID storeId,
        long amount,
        String method,
        long addressId
) {}