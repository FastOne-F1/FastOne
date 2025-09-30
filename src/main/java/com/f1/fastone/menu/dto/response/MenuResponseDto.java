package com.f1.fastone.menu.dto.response;

import java.util.UUID;

public record MenuResponseDto(
        UUID id,
        String name,
        String description,
        int price,
        boolean soldOut,
        boolean option,
        String imageUrl
) {}
