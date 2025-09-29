package com.f1.fastone.review.dto.res;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReplyResponseDto(

	UUID replyId,

	String content,

	String ownerName,

	LocalDateTime createdAt,

	LocalDateTime updatedAt

) {}