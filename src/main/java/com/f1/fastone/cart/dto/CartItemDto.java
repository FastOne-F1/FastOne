package com.f1.fastone.cart.dto;

public record CartItemDto(
        String storeId,
        String menuId,
        int quantity,
        long priceView,
        long createdAt
) {}