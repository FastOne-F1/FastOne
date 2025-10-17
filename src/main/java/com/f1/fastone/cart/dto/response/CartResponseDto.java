package com.f1.fastone.cart.dto.response;

import java.util.List;

public record CartResponseDto(
        String storeId,
        String storeName,
        List<CartItemResponseDto> items
) {
    public static CartResponseDto from(String storeId, String storeName, List<CartItemResponseDto> items) {
        return new CartResponseDto(storeId, storeName, items);
    }
}