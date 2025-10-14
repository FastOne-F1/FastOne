package com.f1.fastone.payment.dto.response;

import com.f1.fastone.payment.entity.Payment;
import java.util.UUID;

public record PaymentCreateResponseDto(
        UUID paymentId
) {
    public static PaymentCreateResponseDto from(Payment payment) {
        return new PaymentCreateResponseDto(payment.getId());
    }
}
