package com.f1.fastone.cart.service;

import com.f1.fastone.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;

    public void initCart(String userId, String storeId) {
        cartRepository.createCart(userId, storeId);
    }
}