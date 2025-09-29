package com.f1.fastone.review.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.store.entity.Store;

@Component
public class ReviewValidator {

	// 리뷰 작성 전: 주문 소유자 & 중복 리뷰 체크
	public void validateReviewCreation(Order order, UUID userId) {
		if (!order.getUser().getId().equals(userId)) {
			throw new ServiceException(ErrorCode.ORDER_ACCESS_DENIED);
		}
		if (order.getReview() != null) {
			throw new ServiceException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}

	// 리뷰 소유자 확인
	public void validateReviewOwner(Review review, UUID userId) {
		if (!review.getUser().getId().equals(userId)) {
			throw new ServiceException(ErrorCode.REVIEW_ACCESS_DENIED);
		}
	}

	// 사장님 대댓글 권한 확인
	public void validateStoreOwner(Store store, UUID userId) {
		if (!store.getOwner().getId().equals(userId)) {
			throw new ServiceException(ErrorCode.ACCESS_DENIED);
		}
	}

}
