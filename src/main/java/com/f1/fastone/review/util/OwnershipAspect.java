package com.f1.fastone.review.util;

import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.util.CheckOwner;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class OwnershipAspect {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final ReviewValidator reviewValidator;

	@Before("@annotation(checkOwner)")
	public void validateOwnership(JoinPoint joinPoint, CheckOwner checkOwner) {
		Object[] args = joinPoint.getArgs();
		String username = (String)args[0];

		switch (checkOwner.type()) {
			case REVIEW -> {
				UUID reviewId = (UUID)args[1];
				Review review = reviewRepository.findById(reviewId)
						.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
				reviewValidator.validateReviewOwner(review, username);
			}
			case ORDER -> {
				ReviewCreateRequestDto dto = (ReviewCreateRequestDto)args[1];
				Order order = orderRepository.findById(dto.orderId())
						.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
				reviewValidator.validateReviewCreation(order, username);
			}
			case STORE -> {
				UUID reviewId = (UUID)args[1];
				Review review = reviewRepository.findById(reviewId)
						.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
				reviewValidator.validateStoreOwner(review.getStore(), username);
			}
		}
	}
}