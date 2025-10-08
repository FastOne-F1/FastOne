package com.f1.fastone.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.f1.fastone.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
