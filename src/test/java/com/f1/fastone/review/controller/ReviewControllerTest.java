package com.f1.fastone.review.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.f1.fastone.util.TestUserFactory;
import com.f1.fastone.common.dto.PageResponse;
import com.f1.fastone.review.dto.req.ReplyRequestDto;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.req.ReviewUpdateRequestDto;
import com.f1.fastone.review.dto.res.ReplyResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.service.ReviewService;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
	"spring.aop.auto=false"
})
@AutoConfigureMockMvc
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ReviewService reviewService;

	@TestConfiguration
	static class MockConfig {
		@Bean
		@Primary
		ReviewService reviewService() {
			return Mockito.mock(ReviewService.class);
		}
	}

	@BeforeEach
	void setUp() {
		User mockUser = User.builder()
			.username("testUser")
			.password("encoded")
			.email("test@example.com")
			.role(UserRole.CUSTOMER)
			.build();

		TestUserFactory.setSecurityContext(mockUser);
	}

	@AfterEach
	void tearDown() {
		TestUserFactory.clearSecurityContext();
	}

	@Test
	@DisplayName("리뷰 생성 요청 성공")
	void createReview() throws Exception {
		UUID orderId = UUID.randomUUID();
		ReviewCreateRequestDto request = new ReviewCreateRequestDto(orderId, 5, "맛있어요!");
		ReviewResponseDto response = new ReviewResponseDto(
			UUID.randomUUID(), 5, "맛있어요!", "testUser",
			"Test Store", LocalDateTime.now(), LocalDateTime.now(),
			null, null
		);

		Mockito.when(reviewService.createReview(eq("testUser"), any(ReviewCreateRequestDto.class)))
			.thenReturn(response);

		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").value("맛있어요!"));
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview() throws Exception {
		UUID reviewId = UUID.randomUUID();
		ReviewUpdateRequestDto request = new ReviewUpdateRequestDto(4, "괜찮았어요");
		ReviewResponseDto response = new ReviewResponseDto(
			reviewId, 4, "괜찮았어요", "testUser", "Test Store",
			LocalDateTime.now(), LocalDateTime.now(), null, null
		);

		Mockito.when(reviewService.updateReview(eq("testUser"), eq(reviewId), any(ReviewUpdateRequestDto.class)))
			.thenReturn(response);

		mockMvc.perform(put("/api/reviews/{reviewId}", reviewId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.score").value(4))
			.andExpect(jsonPath("$.data.content").value("괜찮았어요"));
	}

	@Test
	@DisplayName("리뷰 삭제 성공")
	void deleteReview() throws Exception {
		UUID reviewId = UUID.randomUUID();

		mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200));

		Mockito.verify(reviewService).deleteReview("testUser", reviewId);
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공")
	void getReview() throws Exception {
		UUID reviewId = UUID.randomUUID();
		ReviewResponseDto response = new ReviewResponseDto(
			reviewId, 3, "보통이에요", "otherUser", "Other Store",
			LocalDateTime.now(), LocalDateTime.now(), null, null
		);

		Mockito.when(reviewService.getReview(reviewId)).thenReturn(response);

		mockMvc.perform(get("/api/reviews/{reviewId}", reviewId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.reviewId").value(reviewId.toString()))
			.andExpect(jsonPath("$.data.content").value("보통이에요"));
	}

	@Test
	@DisplayName("가게별 리뷰 목록 조회 성공")
	void getReviewsByStore() throws Exception {
		UUID storeId = UUID.randomUUID();

		ReviewResponseDto r1 = new ReviewResponseDto(
			UUID.randomUUID(), 5, "굿!", "u1", "Store",
			LocalDateTime.now(), LocalDateTime.now(), null, null
		);
		ReviewResponseDto r2 = new ReviewResponseDto(
			UUID.randomUUID(), 4, "괜찮음", "u2", "Store",
			LocalDateTime.now(), LocalDateTime.now(), null, null
		);

		Page<ReviewResponseDto> page = new PageImpl<>(
			List.of(r1, r2),
			PageRequest.of(0, 10),
			2
		);
		PageResponse<ReviewResponseDto> response = PageResponse.of(page);

		Mockito.when(reviewService.getReviewsByStore(eq(storeId), any()))
			.thenReturn(response);

		mockMvc.perform(get("/api/reviews/stores/{storeId}", storeId)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalElements").value(2))
			.andExpect(jsonPath("$.data.content[0].content").value("굿!"))
			.andExpect(jsonPath("$.data.content[1].content").value("괜찮음"))
			.andExpect(jsonPath("$.data.content[0].username").value("u1"))
			.andExpect(jsonPath("$.data.content[1].username").value("u2"));
	}

	@Test
	@DisplayName("사장님 대댓글 작성 성공")
	void addReply() throws Exception {
		UUID reviewId = UUID.randomUUID();
		ReplyRequestDto request = new ReplyRequestDto("사장님 댓글입니다.");
		ReplyResponseDto response = new ReplyResponseDto("사장님 댓글입니다.", LocalDateTime.now(), LocalDateTime.now());

		Mockito.when(reviewService.addReply(eq("testUser"), eq(reviewId), any(ReplyRequestDto.class)))
			.thenReturn(response);

		mockMvc.perform(post("/api/reviews/{reviewId}/reply", reviewId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").value("사장님 댓글입니다."));
	}

	@Test
	@DisplayName("사장님 대댓글 수정 성공")
	void updateReply() throws Exception {
		UUID reviewId = UUID.randomUUID();
		ReplyRequestDto request = new ReplyRequestDto("수정된 대댓글");
		ReplyResponseDto response = new ReplyResponseDto("수정된 대댓글", LocalDateTime.now(), LocalDateTime.now());

		Mockito.when(reviewService.updateReply(eq("testUser"), eq(reviewId), any(ReplyRequestDto.class)))
			.thenReturn(response);

		mockMvc.perform(put("/api/reviews/{reviewId}/reply", reviewId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").value("수정된 대댓글"));
	}

	@Test
	@DisplayName("사장님 대댓글 삭제 성공")
	void deleteReply() throws Exception {
		UUID reviewId = UUID.randomUUID();

		mockMvc.perform(delete("/api/reviews/{reviewId}/reply", reviewId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200));

		Mockito.verify(reviewService).deleteReply("testUser", reviewId);
	}
}