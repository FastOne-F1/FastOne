package com.f1.fastone.review.util;

import org.springframework.stereotype.Component;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.store.entity.Store;

@Component
public class ReviewValidator {

	// 리뷰 작성 전: 주문 소유자 & 중복 리뷰 체크
	public void validateReviewCreation(Order order, String username) {
		if (!order.getUser().getUsername().equals(username)) {
			throw new ServiceException(ErrorCode.ORDER_ACCESS_DENIED);
		}
		if (order.getReview() != null) {
			throw new ServiceException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}

	// 리뷰 소유자 확인
	public void validateReviewOwner(Review review, String username) {
		if (!review.getUser().getUsername().equals(username)) {
			throw new ServiceException(ErrorCode.REVIEW_ACCESS_DENIED);
		}
	}

	// 사장님 대댓글 권한 확인
	public void validateStoreOwner(Store store, String username) {
		if (!store.getOwner().getUsername().equals(username)) {
			throw new ServiceException(ErrorCode.ACCESS_DENIED);
		}
	}
}