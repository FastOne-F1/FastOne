package com.f1.fastone.payment.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentConfirmRequestDto(
    String paymentKey,
    UUID paymentId,
    long amount,
    LocalDateTime approvedAt
) {}
