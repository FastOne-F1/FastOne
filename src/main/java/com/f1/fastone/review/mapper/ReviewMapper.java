package com.f1.fastone.review.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.f1.fastone.order.entity.Order;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.res.OrderMenuSummaryDto;
import com.f1.fastone.review.dto.res.OrderSummaryDto;
import com.f1.fastone.review.dto.res.ReplyResponseDto;
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
			ReplyResponseDto.from(review),
			review.getOrder() != null ? toOrderSummaryDto(review.getOrder()) : null
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
}