package com.f1.fastone.cart.service;

import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    public ItemCreateResponseDto addItem(String userId, UUID storeId, ItemCreateRequestDto requestDto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        Menu menu = menuRepository.findByIdAndStoreId(requestDto.menuId(), storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

        Instant now = Instant.now();
        LocalDateTime addedAt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        String json = String.format("{\"n\":\"%s\",\"img\":\"%s\",\"p\":%d,\"q\":%d,\"a\":%d}",
                menu.getName(), menu.getImageUrl(), menu.getPrice(), requestDto.quantity(), now.toEpochMilli());
        cartRepository.addMenu(userId, store, String.valueOf(requestDto.menuId()), json);

        return ItemCreateResponseDto.from(menu, requestDto.quantity(), addedAt);
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

    public List<CartResponseDto> getCart(String userId) {
        return cartRepository.findAllByUser(userId);
    }
}