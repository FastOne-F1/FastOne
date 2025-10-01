package com.f1.fastone.cart.repository;

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

    public CartRepository(StringRedisTemplate rt) {
        this.redisTemplate = rt;
        this.hash = rt.opsForHash();
        this.set = rt.opsForSet();
    }

    public void createCart(String userId, String storeId) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, storeId);

        set.add(idx, storeId);

        // 해시 키가 없으면 더미 필드로 빈 해시 생성 (첫 아이템 추가 시 __init 제거)
        if (!redisTemplate.hasKey(cart)) {
            hash.put(cart, "__init", "1");
        }

        redisTemplate.expire(idx, IDX_TTL);
        redisTemplate.expire(cart, CART_TTL);
    }

    public void addMenu(String userId, String storeId, String menuId, String jsonValue) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, storeId);

        set.add(idx, storeId);

        if (hash.hasKey(cart, "__init")) {
            hash.delete(cart, "__init");
        }

        hash.put(cart, menuId, jsonValue);

        redisTemplate.expire(idx, IDX_TTL);
        redisTemplate.expire(cart, CART_TTL);
    }

    public void removeMenu(String userId, String storeId, String menuId) {
        String cart = cartKey(userId, storeId);
        hash.delete(cart, menuId);
        redisTemplate.expire(cart, CART_TTL);
    }

    public void clearCart(String userId, String storeId) {
        String idx = idxKey(userId);
        String cart = cartKey(userId, storeId);

        redisTemplate.delete(cart);
        set.remove(idx, storeId);
        redisTemplate.expire(idx, IDX_TTL);
    }
}