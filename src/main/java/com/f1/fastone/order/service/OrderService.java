package com.f1.fastone.order.service;

import com.f1.fastone.cart.entity.Cart;
import com.f1.fastone.cart.entity.CartItem;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderItem;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.order.dto.request.OrderRequestDto;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponseDto createOrder(String username, OrderRequestDto requestDto) {
        // User, 가게, 장바구니 조회
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));
        Cart cart = cartRepository.findById(requestDto.getCartId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));

        // Order 생성
        Order order = requestDto.toEntity(user, store);
        orderRepository.save(order);

        // Order Item 생성
        List<CartItem> cartItems = cart.getItems();
        List<OrderItem> orderItems = convertCartToOrder(order, cartItems);
        List<OrderItemDto> orderItemDtos = orderItems.stream().map(OrderItemDto::from).toList();
        order.setOrderItems(orderItems);

        return new OrderResponseDto(order.getCreatedAt(), store.getName(), orderItemDtos, requestDto.getPayment());

    }

    private List<OrderItem> convertCartToOrder(Order order, List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    Menu menu = cartItem.getMenu();
                    int quantity = cartItem.getQuantity();
                    return OrderItem.builder()
                            .menuName(menu.getName())
                            .quantity(quantity)
                            .price(menu.getPrice() * quantity)
                            .order(order)
                            .menu(menu)
                            .build();
                    })
                    .toList();
    }

}
