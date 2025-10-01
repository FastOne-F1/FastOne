package com.f1.fastone.review.service;

import com.f1.fastone.util.TestOrderFactory;
import com.f1.fastone.util.TestReviewFactory;
import com.f1.fastone.util.TestStoreFactory;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.review.dto.req.ReplyRequestDto;
import com.f1.fastone.review.dto.req.ReviewCreateRequestDto;
import com.f1.fastone.review.dto.req.ReviewUpdateRequestDto;
import com.f1.fastone.review.dto.res.ReplyResponseDto;
import com.f1.fastone.review.dto.res.ReviewResponseDto;
import com.f1.fastone.review.entity.Review;
import com.f1.fastone.review.mapper.ReviewMapper;
import com.f1.fastone.review.repository.ReviewRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.entity.StoreCategory;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ReviewServiceTest {

	@Mock private ReviewRepository reviewRepository;
	@Mock private OrderRepository orderRepository;
	@Mock private StoreRatingService storeRatingService;
	@Mock private ReviewMapper reviewMapper;

	@InjectMocks private ReviewService reviewService;

	private AutoCloseable closeable;

	private Store testStore;
	private Order testOrder;
	private Review testReview;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);

		User testUser = User.builder()
			.username("testUser")
			.email("test@example.com")
			.password("encoded")
			.role(UserRole.CUSTOMER)
			.build();

		StoreCategory category = StoreCategory.of("한식");
		testStore = TestStoreFactory.createStore(testUser, category);
		testOrder = TestOrderFactory.createOrder(testUser, testStore);
		testReview = TestReviewFactory.createReview(testUser, testStore, testOrder, 5, "맛있어요!");
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	@DisplayName("리뷰 생성 성공")
	void createReview_success() {
		// given
		UUID orderId = UUID.randomUUID();
		ReviewCreateRequestDto dto = new ReviewCreateRequestDto(orderId, 5, "맛있어요!");
		given(orderRepository.findById(orderId)).willReturn(Optional.of(testOrder));
		given(reviewMapper.toEntity(dto, testOrder)).willReturn(testReview);
		given(reviewRepository.save(any(Review.class))).willReturn(testReview);
		given(reviewMapper.toDto(any(Review.class))).willReturn(
			new ReviewResponseDto(testReview.getId(), 5, "맛있어요!", "testUser", "테스트 가게",
				LocalDateTime.now(), LocalDateTime.now(), null, null)
		);

		// when
		ReviewResponseDto response = reviewService.createReview("testUser", dto);

		// then
		assertThat(response.content()).isEqualTo("맛있어요!");
		then(storeRatingService).should().increaseRating(testOrder.getStore(), 5);
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 주문 없음")
	void createReview_orderNotFound() {
		UUID orderId = UUID.randomUUID();
		given(orderRepository.findById(orderId)).willReturn(Optional.empty());

		assertThatThrownBy(() -> reviewService.createReview("testUser",
			new ReviewCreateRequestDto(orderId, 5, "맛")))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview_success() {
		UUID reviewId = UUID.randomUUID();
		ReviewUpdateRequestDto dto = new ReviewUpdateRequestDto(4, "괜찮음");
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));
		given(reviewMapper.toDto(any(Review.class))).willReturn(
			new ReviewResponseDto(reviewId, 4, "괜찮음", "testUser", "테스트 가게",
				LocalDateTime.now(), LocalDateTime.now(), null, null)
		);

		ReviewResponseDto response = reviewService.updateReview("testUser", reviewId, dto);

		assertThat(response.score()).isEqualTo(4);
		assertThat(response.content()).isEqualTo("괜찮음");
		then(storeRatingService).should().updateRating(testStore, testReview.getScore(), testReview.getScore());
	}

	@Test
	@DisplayName("리뷰 삭제 성공")
	void deleteReview_success() {
		UUID reviewId = UUID.randomUUID();
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));

		reviewService.deleteReview("testUser", reviewId);

		then(storeRatingService).should().decreaseRating(testStore, testReview.getScore());
		then(reviewRepository).should().delete(testReview);
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공")
	void getReview_success() {
		UUID reviewId = UUID.randomUUID();
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));
		given(reviewMapper.toDto(testReview)).willReturn(
			new ReviewResponseDto(reviewId, 5, "맛있어요!", "testUser", "테스트 가게",
				LocalDateTime.now(), LocalDateTime.now(), null, null)
		);

		ReviewResponseDto response = reviewService.getReview(reviewId);

		assertThat(response.content()).isEqualTo("맛있어요!");
	}

	@Test
	@DisplayName("가게별 리뷰 목록 조회 성공")
	void getReviewsByStore_success() {
		UUID storeId = UUID.randomUUID();
		Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
		given(reviewRepository.findByStoreId(eq(storeId), any(Pageable.class)))
			.willReturn(reviewPage);
		given(reviewMapper.toDto(testReview)).willReturn(
			new ReviewResponseDto(testReview.getId(), 5, "맛있어요!", "testUser", "테스트 가게",
				LocalDateTime.now(), LocalDateTime.now(), null, null)
		);

		var response = reviewService.getReviewsByStore(storeId, PageRequest.of(0, 10));

		assertThat(response.content()).hasSize(1);
		assertThat(response.content().get(0).content()).isEqualTo("맛있어요!");
	}

	@Test
	@DisplayName("사장님 대댓글 작성 성공")
	void addReply_success() {
		UUID reviewId = UUID.randomUUID();
		ReplyRequestDto dto = new ReplyRequestDto("사장님 댓글");
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));

		ReplyResponseDto response = reviewService.addReply("owner", reviewId, dto);

		assertThat(response.content()).isEqualTo("사장님 댓글");
	}

	@Test
	@DisplayName("사장님 대댓글 수정 성공")
	void updateReply_success() {
		UUID reviewId = UUID.randomUUID();
		ReplyRequestDto dto = new ReplyRequestDto("수정된 댓글");
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));

		ReplyResponseDto response = reviewService.updateReply("owner", reviewId, dto);

		assertThat(response.content()).isEqualTo("수정된 댓글");
	}

	@Test
	@DisplayName("사장님 대댓글 삭제 성공")
	void deleteReply_success() {
		UUID reviewId = UUID.randomUUID();
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(testReview));

		reviewService.deleteReply("owner", reviewId);

		assertThat(testReview.getReplyContent()).isNull();
	}
}
