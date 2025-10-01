package com.f1.fastone.review.dto.req;

public record ReviewUpdateRequestDto(
	int score,
	String content
) { }