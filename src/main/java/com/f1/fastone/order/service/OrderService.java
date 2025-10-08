package com.f1.fastone.order.service;

import com.f1.fastone.cart.entity.Cart;
import com.f1.fastone.cart.entity.CartItem;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import com.f1.fastone.order.dto.request.OrderStatusRequestDto;
import com.f1.fastone.order.dto.response.OrderDetailResponseDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderItem;
import com.f1.fastone.order.entity.OrderStatus;
import com.f1.fastone.order.repository.OrderItemRepository;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.order.dto.request.OrderRequestDto;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponseDto createOrder(String username, OrderRequestDto requestDto) {
        // User(CUSTOMER), 가게, 장바구니 조회
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));
        Cart cart = cartRepository.findById(requestDto.getCartId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));

        // Order 생성
        Order order = requestDto.toEntity(user, store);
        orderRepository.save(order);

        // Order Item 생성
        List<CartItem> cartItems = cart.getItems();
        List<OrderItem> orderItems = convertCartToOrder(order, cartItems);
        List<OrderItemDto> orderItemDtos = orderItems.stream().peek(orderItemRepository::save).map(OrderItemDto::from).toList();

        order.setOrderItems(orderItems);

        PaymentDto paymentDto = new PaymentDto(order.getTotalPrice());
        return new OrderResponseDto(order.getId(), order.getCreatedAt(), order.getStore().getName(), orderItemDtos, paymentDto, order.getStatus());
    }

    @Transactional
    public List<OrderResponseDto> getOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        UserRole role = user.getRole();
        List<Order> orders = null;

        switch (role) {
            case CUSTOMER -> {
                orders = user.getOrders();
            }
            case OWNER -> {
                Store store = storeRepository.findByOwner(user);
                orders = orderRepository.findAllByStore(store);
            }
            case MANAGER -> {
                orders = orderRepository.findAll();
            }
        }

        if (orders == null || orders.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND);
        }

        return orders.stream().map(order ->
                OrderResponseDto.from(
                        order,
                        new PaymentDto(order.getTotalPrice()),
                        order.getOrderItems().stream().map(OrderItemDto::from).toList()
                )
        ).toList();
    }

    @Transactional
    public OrderDetailResponseDto getOrderDetail(String username, UUID orderId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        UserRole role = user.getRole();
        Order order = null;

        switch (role) {
            case CUSTOMER -> {
                order = orderRepository.findByIdAndUser(orderId, user).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_DETAIL_ACCESS_DENIED));
            }
            case OWNER -> {
                Store store = storeRepository.findByOwner(user);
                order = orderRepository.findByIdAndStore(orderId, store).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_DETAIL_ACCESS_DENIED));
            }
            case MANAGER -> {
                order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_DETAIL_ACCESS_DENIED));
            }
        }

        ShipToDto shipToDto = new ShipToDto(
            order.getShipToName(), order.getShipToPhone(), order.getPostalCode(), order.getCity(), order.getAddress(), order.getAddressDetail()
        );
        PaymentDto paymentDto = new PaymentDto(order.getTotalPrice());
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream().map(OrderItemDto::from).toList();

        return OrderDetailResponseDto.from(order, shipToDto, paymentDto, orderItemDtos);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(UserDetailsImpl userDetails, UUID orderId, OrderStatusRequestDto requestDto) {
        User user = userDetails.getUser();
        UserRole role = user.getRole();
        Order order = null;

        switch (role) {
            case CUSTOMER -> {
                throw new ServiceException(ErrorCode.ORDER_UPDATE_DENIED);
            }
            case OWNER -> {
                Store store = storeRepository.findByOwner(user);
                order = orderRepository.findByIdAndStore(orderId, store).orElseThrow(() -> new ServiceException(ErrorCode.ORDER_UPDATE_DENIED));
            }
            case MANAGER -> {
                order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
            }
        }

        OrderStatus status = requestDto.getOrderStatus();
        order.setOrderStatus(status);

        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream().map(OrderItemDto::from).toList();

        PaymentDto paymentDto = new PaymentDto(order.getTotalPrice());

        return new OrderResponseDto(order.getId(), order.getCreatedAt(), order.getStore().getName(), orderItemDtos, paymentDto, order.getStatus());
    }

    @Transactional
    public void deleteOrder(UserDetailsImpl userDetails, UUID orderId) {
        User user = userDetails.getUser();
        UserRole role = user.getRole();

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND)
        );

        switch (role) {
            case CUSTOMER -> {
                LocalDateTime orderCreatedAt = order.getCreatedAt();
                LocalDateTime now = LocalDateTime.now();
                if (Duration.between(orderCreatedAt, now).toMinutes() <= 5) {
                    orderRepository.delete(order);
                } else {
                    throw new ServiceException(ErrorCode.ORDER_DELETE_DENIED);
                }
            }
            case OWNER, MANAGER -> {
                orderRepository.delete(order);
            }
        }
    }

    private List<OrderItem> convertCartToOrder(Order order, List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    Menu menu = cartItem.getMenu();
                    return OrderItem.builder()
                            .menuName(menu.getName())
                            .quantity(cartItem.getQuantity())
                            .price(menu.getPrice())
                            .order(order)
                            .menu(menu)
                            .build();
                })
                .toList();
    }
}
