package com.f1.fastone.cart.dto.response;

import com.f1.fastone.cart.dto.CartRedisItem;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record CartItemResponseDto(
        UUID menuId,
        String menuName,
        String imageUrl,
        long price,
        int quantity,
        LocalDateTime addedAt
) {
    public static CartItemResponseDto from(UUID menuId, CartRedisItem item) {
        LocalDateTime addedAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(item.a()),
                ZoneId.systemDefault()
        );
        return new CartItemResponseDto(menuId, item.n(), item.img(), item.p(), item.q(), addedAt);
    }
}