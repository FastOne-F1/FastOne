package com.f1.fastone.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

	private PageUtils() {}

	public static Pageable createdAtDesc(int page, int size) {
		return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
	}

	public static Pageable updatedAtDesc(int page, int size) {
		return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
	}
}