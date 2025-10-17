package com.f1.fastone.payment.service;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.order.service.OrderService;
import com.f1.fastone.payment.dto.request.PaymentConfirmRequestDto;
import com.f1.fastone.payment.dto.request.PaymentRequestDto;
import com.f1.fastone.payment.dto.response.PaymentCreateResponseDto;
import com.f1.fastone.payment.dto.response.PaymentPrepareResponseDto;
import com.f1.fastone.payment.dto.response.PaymentPrepareUserDto;
import com.f1.fastone.payment.entity.Payment;
import com.f1.fastone.payment.repository.PaymentRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;
import com.f1.fastone.user.repository.UserAddressRepository;
import com.f1.fastone.user.repository.UserRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final UserAddressRepository userAddressRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Transactional(readOnly = true)
    public PaymentPrepareResponseDto preparePayment(String username, UUID storeId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        LocalTime now = LocalTime.now();
        if (now.isBefore(store.getOpenTime()) || now.isAfter(store.getCloseTime())) {
            throw new ServiceException(ErrorCode.STORE_CLOSED, "현재는 주문 가능한 시간이 아닙니다.");
        }

        List<CartItemResponseDto> cartItems = cartService.getCartItems(username, storeId);

        UserAddress defaultAddress = userAddressRepository.findFirstByUserUsernameAndIsDefaultTrue(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));
        PaymentPrepareUserDto userDto = PaymentPrepareUserDto.from(user, defaultAddress);

        return PaymentPrepareResponseDto.of(userDto, store.getName(), cartItems);
    }

    @Transactional
    public PaymentCreateResponseDto createPayment(String username, PaymentRequestDto request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        UserAddress address = userAddressRepository.findById(request.addressId())
                .filter(a -> a.getUser().equals(user))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

        List<CartItemResponseDto> cartItems = cartService.getCartItems(username, store.getId());

        String cartSnapshot;
        try {
            cartSnapshot = objectMapper.writeValueAsString(cartItems);
        } catch (JsonProcessingException e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "장바구니 스냅샷 직렬화에 실패했습니다.");
        }

        Payment payment = Payment.create(request, cartSnapshot, address.getId(), user, store);

        paymentRepository.save(payment);
        cartService.clearCart(username, store.getId());

        return PaymentCreateResponseDto.from(payment);
    }

    @Transactional
    public void confirmPayment(String username, PaymentConfirmRequestDto request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.getAmount().equals(request.amount())) {
            throw new ServiceException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        payment.markSuccess(request);
        paymentRepository.save(payment);

        List<CartItemResponseDto> cartItems;
        try {
            cartItems = objectMapper.readValue(
                    payment.getCartSnapshot(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, CartItemResponseDto.class)
            );
        } catch (JsonProcessingException e) {
            throw new ServiceException(ErrorCode.INTERNAL_SERVER_ERROR, "장바구니 스냅샷 파싱에 실패했습니다.");
        }

        orderService.createOrderFromPayment(username, payment, cartItems);
    }
}