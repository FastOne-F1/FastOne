package com.f1.fastone.cart.repository;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.InternalServerException;
import com.f1.fastone.cart.dto.CartRedisItem;
import com.f1.fastone.store.entity.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;

@Repository
public class CartRepository {

    private static final Duration CART_TTL = Duration.ofDays(7);
    private static final Duration IDX_TTL = Duration.ofDays(30);
    private static final String STORE_NAME_KEY = "__storeName";

    public static String cartKey(String userId, String storeId) { return "cart:" + userId + ":store:" + storeId; }
    public static String idxKey(String userId) { return "cart:" + userId + ":stores"; }

    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hash;
    private final SetOperations<String, String> set;
    private final ObjectMapper objectMapper;

    public CartRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.hash = redisTemplate.opsForHash();
        this.set = redisTemplate.opsForSet();
        this.objectMapper = objectMapper;
    }

    public void addMenu(String userId, Store store, String menuId, CartRedisItem item) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, store.getId().toString());

        hash.putIfAbsent(cart, STORE_NAME_KEY, store.getName());
        set.add(idx, store.getId().toString());

        String jsonValue;
        try {
            jsonValue = objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
        }
        hash.put(cart, menuId, jsonValue);

        redisTemplate.expire(idx, IDX_TTL);
        redisTemplate.expire(cart, CART_TTL);
    }

    public void updateQuantity(String userId, String storeId, String menuId, int quantity) {
        String cart = cartKey(userId, storeId);
        String existingJson = hash.get(cart, menuId);
        if (existingJson == null) {
            throw new EntityNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        try {
            CartRedisItem existingItem = objectMapper.readValue(existingJson, CartRedisItem.class);
            CartRedisItem updatedItem = CartRedisItem.updateQuantity(existingItem, quantity);
            String updatedJson = objectMapper.writeValueAsString(updatedItem);
            hash.put(cart, menuId, updatedJson);
            redisTemplate.expire(cart, CART_TTL);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
        }
    }

    public void removeMenu(String userId, String storeId, String menuId) {
        String cart = cartKey(userId, storeId);
        if (!hash.hasKey(cart, menuId)) {
            throw new EntityNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        hash.delete(cart, menuId);
        redisTemplate.expire(cart, CART_TTL);
    }

    public void clearCart(String userId, String storeId) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, storeId);

        if (!redisTemplate.hasKey(cart)) {
            throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
        }

        redisTemplate.delete(cart);
        set.remove(idx, storeId);
        redisTemplate.expire(idx, IDX_TTL);
    }

    public List<CartResponseDto> findAllByUser(String userId) {
        String idx = idxKey(userId);
        Set<String> storeIds = set.members(idx);
        if (storeIds == null || storeIds.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
        }

        List<CartResponseDto> carts = new ArrayList<>();

        for (String storeId : storeIds) {
            String cart = cartKey(userId, storeId);
            Map<String, String> menus = hash.entries(cart);

            // 장바구니 해시가 없거나 만료된 경우 인덱스에서 제거
            if (menus == null || menus.isEmpty()) {
                set.remove(idx, storeId);
                continue;
            }

            String storeName = menus.remove(STORE_NAME_KEY);
            List<CartItemResponseDto> items = new ArrayList<>();

            menus.forEach((key, value) -> {
                try {
                    CartRedisItem redisItem = objectMapper.readValue(value, CartRedisItem.class);
                    items.add(CartItemResponseDto.from(UUID.fromString(key), redisItem));
                } catch (JsonProcessingException e) {
                    throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
                }
            });

            carts.add(CartResponseDto.from(storeId, storeName, items));
        }

        if (carts.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
        }

        return carts;
    }

    public Map<UUID, CartRedisItem> findByUserAndStore(String userId, String storeId) {
        Map<String, String> cart = hash.entries(cartKey(userId, storeId));
        if (cart.isEmpty()) { throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND); }

        Map<UUID, CartRedisItem> result = new LinkedHashMap<>();
        cart.forEach((key, value) -> {
            if (STORE_NAME_KEY.equals(key)) {
                return;
            }
            try {
                CartRedisItem item = objectMapper.readValue(value, CartRedisItem.class);
                result.put(UUID.fromString(key), item);
            } catch (JsonProcessingException e) {
                throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
            }
        });
        return result;
    }

    public void updateMenu(String userId, String storeId, String menuId, CartRedisItem item) {
        String cart = cartKey(userId, storeId);
        try {
            String json = objectMapper.writeValueAsString(item);
            hash.put(cart, menuId, json);
            redisTemplate.expire(cart, CART_TTL);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(ErrorCode.REDIS_DATA_CORRUPTED);
        }
    }

}