package com.f1.fastone.payment.dto.response;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import java.util.List;

public record PaymentPrepareResponseDto(
        PaymentPrepareUserDto user,
        String storeName,
        List<CartItemResponseDto> cartItems,
        long totalAmount
) {
    public static PaymentPrepareResponseDto of(PaymentPrepareUserDto user, String storeName, List<CartItemResponseDto> cartItems) {
        long total = cartItems.stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();

        return new PaymentPrepareResponseDto(
                user,
                storeName,
                cartItems,
                total
        );
    }
}