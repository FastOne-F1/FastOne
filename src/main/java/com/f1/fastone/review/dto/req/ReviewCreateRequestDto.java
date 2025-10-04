package com.f1.fastone.review.dto.req;

import java.util.UUID;

public record ReviewCreateRequestDto(
	UUID orderId,
	int score,
	String content
) { }