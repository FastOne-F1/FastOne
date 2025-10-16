package com.f1.fastone.review.util;

import java.util.UUID;

import com.f1.fastone.order.repository.OrderRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.common.util.CheckOwner;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.review.repository.ReviewRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final StoreRepository storeRepository;
	private final ReviewValidator reviewValidator;

	@Before(value = "@annotation(checkOwner) && args(username, targetId, ..)", argNames = "checkOwner,username,targetId")
	public void validateOwnership(CheckOwner checkOwner, String username, UUID targetId) {
		switch (checkOwner.type()) {
			case REVIEW -> {
				Review review = reviewRepository.findById(targetId)
					.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
				reviewValidator.validateReviewOwner(review, username);
			}
			case ORDER -> {
				Order order = orderRepository.findById(targetId)
					.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
				reviewValidator.validateReviewCreation(order, username);
			}
			case STORE -> {
				Store store = storeRepository.findById(targetId)
					.orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));
				reviewValidator.validateStoreOwner(store, username);
			}

			default -> throw new ServiceException(ErrorCode.INVALID_TYPE_VALUE);
		}
	}
}