package com.f1.fastone.cart.dto;

import com.f1.fastone.menu.entity.Menu;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record CartRedisItem(
        String n,
        String img,
        int p,
        int q,
        long a
) {
    public static CartRedisItem from(Menu menu, int quantity, LocalDateTime addedAt) {
        return new CartRedisItem(menu.getName(), menu.getImageUrl(), menu.getPrice(), quantity, addedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static CartRedisItem updateQuantity(CartRedisItem original, int newQuantity) {
        return new CartRedisItem(original.n(), original.img(), original.p(), newQuantity, original.a());
    }

    public static CartRedisItem updateFrom(CartRedisItem original, Menu menu) {
        return new CartRedisItem(menu.getName(), menu.getImageUrl(), menu.getPrice(), original.q, original.a);
    }
}