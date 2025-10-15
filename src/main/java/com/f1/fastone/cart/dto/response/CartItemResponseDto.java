package com.f1.fastone.cart.dto.response;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public record CartItemResponseDto(
        String menuId,
        String menuName,
        String imageUrl,
        long priceSnapshot,
        int quantity,
        LocalDateTime addedAt
) {
    public static CartItemResponseDto from(String menuId, Map<String, Object> map) {
        LocalDateTime addedAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(((Number) map.get("a")).longValue()),
                ZoneId.systemDefault()
        );
        return new CartItemResponseDto(
                menuId,
                (String) map.get("n"),
                (String) map.get("img"),
                ((Number) map.get("p")).longValue(),
                ((Number) map.get("q")).intValue(),
                addedAt
        );
    }
}