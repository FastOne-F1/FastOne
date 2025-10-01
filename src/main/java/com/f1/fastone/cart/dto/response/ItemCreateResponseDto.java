package com.f1.fastone.cart.dto.response;

import java.time.LocalDateTime;

public record ItemCreateResponseDto(
    String storeId,
    String menuId,
    int quantity,
    int priceView,
    LocalDateTime addedAt
) {
    public static ItemCreateResponseDto from(String storeId, String menuId, int quantity, int priceView, LocalDateTime addedAt) {
        return new ItemCreateResponseDto(storeId, menuId, quantity, priceView, addedAt);
    }
}
