package com.f1.fastone.review.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.f1.fastone.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

	@EntityGraph(attributePaths = {"user", "store", "order"})
	Page<Review> findByStoreId(UUID storeId, Pageable pageable);

}