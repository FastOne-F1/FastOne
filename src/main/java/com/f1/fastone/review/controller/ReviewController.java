package com.f1.fastone.review.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.dto.ApiResponse;
import com.f1.fastone.common.dto.PageResponse;
import com.f1.fastone.common.util.PageUtils;
import com.f1.fastone.review.dto.req.ReplyRequestDto;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.req.ReviewUpdateRequestDto;
import com.f1.fastone.review.dto.res.ReplyResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ApiResponse<ReviewResponseDto> createReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@Valid @RequestBody ReviewCreateRequestDto requestDto
	) {
		ReviewResponseDto response = reviewService.createReview(userDetails.getUsername(), requestDto);
		return ApiResponse.created(response);
	}

	@PutMapping("/{reviewId}")
	public ApiResponse<ReviewResponseDto> updateReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID reviewId,
		@Valid @RequestBody ReviewUpdateRequestDto requestDto
	) {
		ReviewResponseDto response = reviewService.updateReview(userDetails.getUsername(), reviewId, requestDto);
		return ApiResponse.success(response);
	}

	@DeleteMapping("/{reviewId}")
	public ApiResponse<Void> deleteReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID reviewId
	) {
		reviewService.deleteReview(userDetails.getUsername(), reviewId);
		return ApiResponse.success();
	}

	@GetMapping("/{reviewId}")
	public ApiResponse<ReviewResponseDto> getReview(@PathVariable UUID reviewId) {
		ReviewResponseDto response = reviewService.getReview(reviewId);
		return ApiResponse.success(response);
	}

	@GetMapping("/stores/{storeId}")
	public ApiResponse<PageResponse<ReviewResponseDto>> getReviewsByStore(
		@PathVariable UUID storeId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageUtils.createdAtDesc(page, size);
		PageResponse<ReviewResponseDto> response = reviewService.getReviewsByStore(storeId, pageable);
		return ApiResponse.success(response);
	}

	@PostMapping("/{reviewId}/reply")
	public ApiResponse<ReplyResponseDto> addReply(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID reviewId,
		@Valid @RequestBody ReplyRequestDto requestDto
	) {
		ReplyResponseDto response = reviewService.addReply(userDetails.getUsername(), reviewId, requestDto);
		return ApiResponse.created(response);
	}

	@PutMapping("/{reviewId}/reply")
	public ApiResponse<ReplyResponseDto> updateReply(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID reviewId,
		@Valid @RequestBody ReplyRequestDto requestDto
	) {
		ReplyResponseDto response = reviewService.updateReply(userDetails.getUsername(), reviewId, requestDto);
		return ApiResponse.success(response);
	}

	@DeleteMapping("/{reviewId}/reply")
	public ApiResponse<Void> deleteReply(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID reviewId
	) {
		reviewService.deleteReply(userDetails.getUsername(), reviewId);
		return ApiResponse.success();
	}
}