package com.f1.fastone.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.f1.fastone.store.entity.Store;
import com.f1.fastone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f1.fastone.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByStore(Store store);
    Optional<Order> findByIdAndUser(UUID orderId, User user);
    Optional<Order> findByIdAndStore(UUID orderId, Store store);
}