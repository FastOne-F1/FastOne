package com.f1.fastone.common.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
	List<T> content,
	long totalElements,
	int totalPages,
	int page,
	int size,
	boolean hasNext,
	boolean hasPrevious
) {
	public static <T> PageResponse<T> of(Page<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getNumber(),
			page.getSize(),
			page.hasNext(),
			page.hasPrevious()
		);
	}
}