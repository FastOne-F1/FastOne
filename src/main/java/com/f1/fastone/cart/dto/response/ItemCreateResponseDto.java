package com.f1.fastone.cart.dto.response;

import com.f1.fastone.menu.entity.Menu;
import java.time.LocalDateTime;
import java.util.UUID;

public record ItemCreateResponseDto(
        UUID menuId,
        String menuName,
        String imageUrl,
        int price,
        int quantity,
        LocalDateTime addedAt
) {
    public static ItemCreateResponseDto from(Menu menu, int quantity, LocalDateTime addedAt) {
        return new ItemCreateResponseDto(
                menu.getId(),
                menu.getName(),
                menu.getImageUrl(),
                menu.getPrice(),
                quantity,
                addedAt
        );
    }
}
