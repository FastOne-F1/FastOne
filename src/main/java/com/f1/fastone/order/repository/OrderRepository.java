package com.f1.fastone.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f1.fastone.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {}