package com.f1.fastone.review.dto.res;

import java.time.LocalDateTime;

import com.f1.fastone.review.entity.Review;

public record ReplyResponseDto(

	String content,

	LocalDateTime createdAt,

	LocalDateTime updatedAt

) {
	public static ReplyResponseDto from(Review review) {
		if (review.getReplyContent() == null) return null;

		return new ReplyResponseDto(
			review.getReplyContent(),
			review.getReplyCreatedAt(),
			review.getReplyUpdatedAt()
		);
	}
}