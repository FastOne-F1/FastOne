package com.f1.fastone.order.service;

import com.f1.fastone.cart.entity.Cart;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.order.dto.request.OrderCreateRequestDto;
import com.f1.fastone.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;

    @Transactional
    @CheckOwner(type = OwnershipType.ORDER)
    public void createOrder(String username, OrderCreateRequestDto requestDto) {
        Cart cart = cartRepository.findById(requestDto.getCartId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
        String requestNote = requestDto.getRequestNote();
        ShipToDto shipToDto = requestDto.getShipToDto();
        PaymentDto paymentDto = requestDto.getPaymentDto();

        Review review = reviewRepository.save(reviewMapper.toEntity(requestDto, order));
        storeRatingService.increaseRating(order.getStore(), review.getScore());
        return reviewMapper.toDto(review);
    }
}
