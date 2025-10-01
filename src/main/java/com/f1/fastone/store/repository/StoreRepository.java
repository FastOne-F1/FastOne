package com.f1.fastone.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.f1.fastone.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, UUID> {
}