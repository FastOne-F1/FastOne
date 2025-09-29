package com.f1.fastone.review.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.req.ReviewUpdateRequestDto;
import com.f1.fastone.review.dto.res.ReviewListResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.review.mapper.ReviewMapper;
import com.f1.fastone.review.repository.ReviewRepository;
import com.f1.fastone.review.util.ReviewValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final ReviewMapper reviewMapper;
	private final ReviewValidator reviewValidator;

	@Transactional
	public ReviewResponseDto createReview(UUID userId, ReviewCreateRequestDto requestDto) {
		Order order = orderRepository.findById(requestDto.orderId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));

		reviewValidator.validateReviewCreation(order, userId);
		Review review = reviewRepository.save(reviewMapper.toEntity(requestDto, order));
		return reviewMapper.toDto(review);
	}

	@Transactional
	public ReviewResponseDto updateReview(UUID userId, UUID reviewId, ReviewUpdateRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		reviewValidator.validateReviewOwner(review, userId);
		review.update(requestDto.score(), requestDto.content());
		return reviewMapper.toDto(review);
	}

	@Transactional
	public void deleteReview(UUID userId, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		reviewValidator.validateReviewOwner(review, userId);
		reviewRepository.delete(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponseDto getReview(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
		return reviewMapper.toDto(review);
	}

	@Transactional(readOnly = true)
	public ReviewListResponseDto getReviewsByStore(UUID storeId, Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findByStoreId(storeId, pageable);
		return reviewMapper.toListDto(reviewPage);
	}
}