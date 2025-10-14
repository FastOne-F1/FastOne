package com.f1.fastone.order.service;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.cart.dto.response.CartResponseDto;
import com.f1.fastone.cart.repository.CartRepository;
import com.f1.fastone.cart.service.CartService;
import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
import com.f1.fastone.order.dto.OrderItemDto;
import com.f1.fastone.order.dto.PaymentDto;
import com.f1.fastone.order.dto.ShipToDto;
import com.f1.fastone.order.dto.request.OrderRequestDto;
import com.f1.fastone.order.dto.request.OrderStatusRequestDto;
import com.f1.fastone.order.dto.response.OrderDetailResponseDto;
import com.f1.fastone.order.dto.response.OrderResponseDto;
import com.f1.fastone.order.entity.Order;
import com.f1.fastone.order.entity.OrderItem;
import com.f1.fastone.order.entity.OrderStatus;
import com.f1.fastone.order.repository.OrderItemRepository;
import com.f1.fastone.order.repository.OrderRepository;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final CartService cartService;

    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponseDto createOrder(String username, OrderRequestDto requestDto) {


        // User(CUSTOMER), 가게, 장바구니 조회
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(requestDto.getStoreId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.STORE_NOT_FOUND));

        // from DB to Redis
//        Cart cart = cartJpaRepository.findById(requestDto.getCartId()).orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));
//        syncCartToRedis(cart);

        CartResponseDto cartDto = cartRepository.findByUserAndStore(user.getUsername(), store.getId().toString());

        // Order 생성
        Order order = requestDto.toEntity(user, store);
        orderRepository.save(order);

        // Order Item 생성
        List<OrderItem> orderItems = convertCartToOrder(order, cartDto.items());
        List<OrderItemDto> orderItemDtos = orderItems.stream().peek(orderItemRepository::save).map(OrderItemDto::from).toList();

        order.setOrderItems(orderItems);

        PaymentDto paymentDto = new PaymentDto(order.getTotalPrice());

        // Cart 및 Cart Item 삭제
        cartService.clearCart(user.getUsername(), store.getId());

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
            case MANAGER, MASTER -> {
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
            case MANAGER, MASTER -> {
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
            case MANAGER, MASTER -> {
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
            case OWNER, MANAGER, MASTER -> {
                orderRepository.delete(order);
            }
        }
    }


    private List<OrderItem> convertCartToOrder(Order order, List<CartItemResponseDto> cartItems) {
        return cartItems.stream()
                .map(cartItemDto -> {
                    // 메뉴 엔티티 조회
                    Menu menu = menuRepository.findById(UUID.fromString(cartItemDto.menuId()))
                            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND));

                    // OrderItem 생성
                    return OrderItem.builder()
                            .menuName(menu.getName())
                            .quantity(cartItemDto.quantity())
                            .price((int) cartItemDto.priceSnapshot())
                            .order(order)
                            .menu(menu)
                            .build();
                })
                .toList();
    }


    // from DB to Redis
//    public void syncCartToRedis(Cart cart) {
//        String userId = cart.getUser().getUsername();
//        String storeId = cart.getStore().getId().toString();
//
//        for (CartItem item : cart.getItems()) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("n", item.getMenu().getName());        // 메뉴 이름
//            map.put("p", item.getMenu().getPrice());       // 가격
//            map.put("q", item.getQuantity());              // 수량
//            map.put("a", System.currentTimeMillis());      // 추가 시각 (epoch ms)
//            map.put("img", item.getMenu().getImageUrl());  // 이미지 URL
//
//            try {
//                String jsonValue = objectMapper.writeValueAsString(map);
//                // Redis에 저장
//                cartRepository.addMenu(userId, cart.getStore(), item.getMenu().getId().toString(), jsonValue);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("Redis 동기화 중 JSON 변환 오류", e);
//            }
//        }
//    }
}
