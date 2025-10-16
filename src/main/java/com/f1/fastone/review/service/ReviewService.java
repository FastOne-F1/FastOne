package com.f1.fastone.review.service;

import com.f1.fastone.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.f1.fastone.common.dto.PageResponse;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.util.CheckOwner;
import com.f1.fastone.common.util.OwnershipType;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.review.dto.req.ReplyRequestDto;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.req.ReviewUpdateRequestDto;
import com.f1.fastone.review.dto.res.ReplyResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.review.mapper.ReviewMapper;
import com.f1.fastone.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewSummaryService reviewSummaryService;
	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final StoreRatingService storeRatingService;
	private final ReviewMapper reviewMapper;

	@Transactional
	@CheckOwner(type = OwnershipType.ORDER)
	public ReviewResponseDto createReview(String username, ReviewCreateRequestDto requestDto) {
		Order order = orderRepository.findById(requestDto.orderId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));

		Review review = reviewRepository.save(reviewMapper.toEntity(requestDto, order));
		storeRatingService.increaseRating(order.getStore(), review.getScore());
		return reviewMapper.toDto(review);
	}

	@Transactional
	@CheckOwner(type = OwnershipType.REVIEW)
	public ReviewResponseDto updateReview(String username, UUID reviewId, ReviewUpdateRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		review.update(requestDto.score(), requestDto.content());
		storeRatingService.updateRating(review.getStore(), review.getScore(), review.getScore());
		return reviewMapper.toDto(review);
	}

	@Transactional
	@CheckOwner(type = OwnershipType.REVIEW)
	public void deleteReview(String username, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		storeRatingService.decreaseRating(review.getStore(), review.getScore());
		reviewRepository.delete(review);
	}

	@Transactional(readOnly = true)
	public ReviewResponseDto getReview(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
		return reviewMapper.toDto(review);
	}

	@Transactional(readOnly = true)
	public PageResponse<ReviewResponseDto> getReviewsByStore(User user, UUID storeId, Pageable pageable) {

		Page<Review> page = reviewRepository.findByStoreId(storeId, pageable);
		List<Review> latestReviews = reviewRepository.findTop10ByStoreIdOrderByCreatedAtDesc(storeId);

		String summary = Optional.ofNullable(reviewSummaryService.getCachedSummary(storeId))
			.orElseGet(() -> {
				if (latestReviews.isEmpty()) return null;
				return reviewSummaryService.summarizeRecentReviews(user, storeId, latestReviews);
			});

		return PageResponse.of(
			page.map(review -> reviewMapper.toDtoWithSummary(review, summary))
		);
	}

	@Transactional
	@CheckOwner(type = OwnershipType.STORE)
	public ReplyResponseDto addReply(String username, UUID reviewId, ReplyRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		review.addReply(requestDto.content());
		return ReplyResponseDto.from(review);
	}

	@Transactional
	@CheckOwner(type = OwnershipType.STORE)
	public ReplyResponseDto updateReply(String username, UUID reviewId, ReplyRequestDto requestDto) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		review.updateReply(requestDto.content());
		return ReplyResponseDto.from(review);
	}

	@Transactional
	@CheckOwner(type = OwnershipType.STORE)
	public void deleteReply(String username, UUID reviewId) {
		reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND))
			.deleteReply();
	}
}