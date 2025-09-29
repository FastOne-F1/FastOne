package com.f1.fastone.review.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.res.OrderMenuSummaryDto;
import com.f1.fastone.review.dto.res.OrderSummaryDto;
import com.f1.fastone.review.dto.res.ReviewListResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.entity.Review;

@Component
public class ReviewMapper {

	public ReviewResponseDto toDto(Review review) {
		return new ReviewResponseDto(
			review.getId(),
			review.getScore(),
			review.getContent(),
			review.getUser().getUsername(),
			review.getStore().getName(),
			review.getCreatedAt(),
			review.getUpdatedAt(),
			null,
			review.getOrder() != null ? toOrderSummaryDto(review.getOrder()) : null
			// TODO:: N + 1 구간 추후 수정 반드시 필요
		);
	}

	public Review toEntity(ReviewCreateRequestDto dto, Order order) {
		return Review.builder()
			.score(dto.score())
			.content(dto.content())
			.user(order.getUser())
			.store(order.getStore())
			.order(order)
			.build();
	}

	public OrderSummaryDto toOrderSummaryDto(Order order) {
		List<OrderMenuSummaryDto> menuSummaries = order.getOrderItems().stream()
			.map(item -> new OrderMenuSummaryDto(
				item.getMenu().getId(),
				item.getMenuName(),
				item.getPrice(),
				item.getQuantity(),
				item.getMenu().getCategory() != null
					? item.getMenu().getCategory().getMenuCategoryName()
					: null
			))
			.toList();

		return new OrderSummaryDto(order.getCreatedAt(), order.getTotalPrice(), menuSummaries);
	}

	public ReviewListResponseDto toListDto(Page<Review> reviewPage) {
		List<ReviewResponseDto> reviewDtos = reviewPage.getContent().stream()
			.map(this::toDto)
			.toList();

		return new ReviewListResponseDto(
			reviewDtos,
			reviewPage.getTotalElements(),
			reviewPage.getNumber(),
			reviewPage.getSize()
		);
	}
}