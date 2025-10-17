package com.f1.fastone.cart.service;

import com.f1.fastone.cart.dto.request.ItemCreateRequestDto;
import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.dto.response.ItemCreateResponseDto;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.cart.dto.CartRedisItem;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;

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

        LocalDateTime addedAt = LocalDateTime.now();
        CartRedisItem cartData = CartRedisItem.from(menu, requestDto.quantity(), addedAt);
        cartRepository.addMenu(userId, store, requestDto.menuId().toString(), cartData);

        return ItemCreateResponseDto.from(menu, requestDto.quantity(), addedAt);
    }

    public void setQuantity(String userId, UUID storeId, String menuId, int quantity) {
        cartRepository.updateQuantity(userId, storeId.toString(), menuId, quantity);
    }

    public void removeItem(String userId, UUID storeId, String menuId) {
        cartRepository.removeMenu(userId, storeId.toString(), menuId);
    }

    public void clearCart(String userId, UUID storeId) {
        cartRepository.clearCart(userId, storeId.toString());
    }

    public List<CartResponseDto> getCart(String userId) {
        return cartRepository.findAllByUser(userId);
    }

    public List<CartItemResponseDto> getCartItems(String userId, UUID storeId) {
        Map<UUID, CartRedisItem> items = cartRepository.findByUserAndStore(userId, storeId.toString());
        if (items == null || items.isEmpty()) throw new ServiceException(ErrorCode.CART_NOT_FOUND);

        List<UUID> menuIds = new ArrayList<>(items.keySet());
        Map<UUID, Menu> menuMap = menuRepository.findAllById(menuIds).stream().collect(Collectors.toMap(Menu::getId, m -> m));

        List<CartItemResponseDto> result = new ArrayList<>();
        items.forEach((menuId, redisItem) -> {
            Menu menu = menuMap.get(menuId);
            if (menu == null || menu.isSoldOut()) throw new ServiceException(ErrorCode.MENU_NOT_FOUND, "존재하지 않거나 판매 중단된 메뉴입니다.");

            // 가격 불일치 시 Redis 갱신
            if (menu.getPrice() != redisItem.p()) {
                CartRedisItem updatedRedisItem = CartRedisItem.updateFrom(redisItem, menu);
                cartRepository.updateMenu(userId, storeId.toString(), menuId.toString(), updatedRedisItem);
                redisItem = updatedRedisItem;
            }

            result.add(CartItemResponseDto.from(menuId, redisItem));
        });

        return result;
    }
}