package com.f1.fastone.store.repository;

import com.f1.fastone.store.entity.StoreRating;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRatingRepository extends JpaRepository<StoreRating, UUID> {
}