package com.f1.fastone.cart.repository;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.store.entity.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public static String cartKey(String userId, String storeId){ return "cart:" + userId + ":store:" + storeId; }
    public static String idxKey(String userId)             { return "cart:" + userId + ":stores"; }

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

    public void addMenu(String userId, Store store, String menuId, String jsonValue) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, store.getId().toString());

        hash.putIfAbsent(cart, "__storeName", store.getName());
        set.add(idx, store.getId().toString());

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
            Map<String, Object> itemMap = objectMapper.readValue(existingJson, Map.class);
            itemMap.put("q", quantity);
            String updatedJson = objectMapper.writeValueAsString(itemMap);
            hash.put(cart, menuId, updatedJson);
            redisTemplate.expire(cart, CART_TTL);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
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
            Map<String, String> entries = hash.entries(cart);

            // 장바구니 해시가 없거나 만료된 경우 인덱스에서 제거
            if (entries == null || entries.isEmpty()) {
                set.remove(idx, storeId);
                continue;
            }

            String storeName = entries.remove("__storeName");
            List<CartItemResponseDto> items = new ArrayList<>();

            for (Map.Entry<String, String> e : entries.entrySet()) {
                try {
                    Map<String, Object> map = objectMapper.readValue(e.getValue(), Map.class);
                    items.add(CartItemResponseDto.from(e.getKey(), map));
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }

            carts.add(CartResponseDto.from(storeId, storeName, items));
        }

        if (carts.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
        }

        return carts;
    }
}