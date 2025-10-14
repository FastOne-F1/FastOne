package com.f1.fastone.payment.service;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.payment.dto.request.PaymentRequestDto;
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
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final UserAddressRepository userAddressRepository;

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
    public void createPayment(String username, PaymentRequestDto request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        Payment payment = Payment.create(request, user, store);
        paymentRepository.save(payment);
    }
}