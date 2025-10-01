package com.f1.fastone.cart.service;

import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;

    public void initCart(String userId, UUID storeId) {
        cartRepository.createCart(userId, String.valueOf(storeId));
    }

    public ItemCreateResponseDto addItem(String userId, UUID storeId, ItemCreateRequestDto requestDto) {
        Menu menu = menuRepository.findByIdAndStoreId(requestDto.menuId(), storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        int priceView = menu.getPrice();
        Instant now = Instant.now();
        LocalDateTime addedAt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        long addedAtEpoch = now.toEpochMilli();
        String json = compact(requestDto.quantity(), priceView, addedAtEpoch);

        cartRepository.addMenu(userId, String.valueOf(storeId), String.valueOf(requestDto.menuId()), json);

        return ItemCreateResponseDto.from(String.valueOf(storeId), String.valueOf(requestDto.menuId()), requestDto.quantity(), priceView, addedAt);
    }

    public void setQuantity(String userId, UUID storeId, String menuId, int quantity) {
        cartRepository.updateQuantity(userId, String.valueOf(storeId), menuId, quantity);
    }

    public void removeItem(String userId, UUID storeId, String menuId) {
        cartRepository.removeMenu(userId, String.valueOf(storeId), menuId);
    }

    public void clearCart(String userId, UUID storeId) {
        cartRepository.clearCart(userId, String.valueOf(storeId));
    }

    private String compact(int quantity, int price, long addedAt) {
        // {"q":2,"p":10000,"a":1696170030000}
        return String.format("{\"q\":%d,\"p\":%d,\"a\":%d}", quantity, price, addedAt);
    }
}