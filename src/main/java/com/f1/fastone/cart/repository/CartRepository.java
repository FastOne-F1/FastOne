package com.f1.fastone.cart.repository;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
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

    public CartRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hash = redisTemplate.opsForHash();
        this.set = redisTemplate.opsForSet();
    }

    public void addMenu(String userId, String storeId, String menuId, String jsonValue) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, storeId);

        // 장바구니(해시)가 없으면 새로 생성
        if (!redisTemplate.hasKey(cart)) {
            hash.put(cart, "__init", "1");
        }

        set.add(idx, storeId);

        if (hash.hasKey(cart, "__init")) {
            hash.delete(cart, "__init");
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
            Map<String, Object> itemMap = new ObjectMapper().readValue(existingJson, Map.class);
            itemMap.put("q", quantity);
            String updatedJson = new ObjectMapper().writeValueAsString(itemMap);
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
}