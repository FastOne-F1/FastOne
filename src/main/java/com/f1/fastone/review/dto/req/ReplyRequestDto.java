package com.f1.fastone.review.dto.req;

import jakarta.validation.constraints.NotBlank;

public record ReplyRequestDto(

	@NotBlank
	String content

) {}