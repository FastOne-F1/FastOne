package com.f1.fastone.review.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponseDto(

	UUID reviewId,

	int score,

	String content,

	String username,

	String storeName,

	LocalDateTime createdAt,

	LocalDateTime updatedAt,

	ReplyResponseDto reply, // 있을 경우에만 필드 존재

	OrderSummaryDto order,

	String summary
) {}