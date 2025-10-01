package com.f1.fastone.cart.repository;

import com.f1.fastone.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


public interface CartRepository extends JpaRepository<Cart, UUID> {
}
