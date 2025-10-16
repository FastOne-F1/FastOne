package com.f1.fastone.order.service;

import com.f1.fastone.cart.dto.response.CartItemResponseDto;
import com.f1.fastone.common.auth.security.UserDetailsImpl;
import com.f1.fastone.common.exception.ErrorCode;
import com.f1.fastone.common.exception.custom.EntityNotFoundException;
import com.f1.fastone.common.exception.custom.ServiceException;
import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.menu.repository.MenuRepository;
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
import com.f1.fastone.payment.entity.Payment;
import com.f1.fastone.store.entity.Store;
import com.f1.fastone.store.repository.StoreRepository;
import com.f1.fastone.user.entity.User;
import com.f1.fastone.user.entity.UserAddress;
import com.f1.fastone.user.entity.UserRole;
import com.f1.fastone.user.repository.UserAddressRepository;
import com.f1.fastone.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserAddressRepository userAddressRepository;

    @Transactional(TxType.REQUIRES_NEW)
    public void createOrderFromPayment(String username, Payment payment, List<CartItemResponseDto> cartItems) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        UserAddress address = userAddressRepository.findById(payment.getAddressId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

        Order order = Order.create(user, payment, address);

        orderRepository.save(order);

        List<OrderItem> orderItems = convertCartToOrder(order, cartItems);
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);
    }

    @Transactional
    public List<OrderResponseDto> getOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        UserRole role = user.getRole();
        List<Order> orders = null;

//        if (keyword == null || keyword.isEmpty()) {
//            Page<Order> orders = orderRepository.findAllByUser(user);
//
//            Page<OrderResponseDto> responseDtos = orders.map(order ->
//                    OrderResponseDto.from(
//                            order,
//                            new PaymentDto(order.getTotalPrice()),
//                            order.getOrderItems().stream().map(OrderItemDto::from).toList()
//                    )
//            );
//            return PageResponse.of(responseDtos);
//        }

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
        List<UUID> menuIds = cartItems.stream()
                .map(CartItemResponseDto::menuId)
                .toList();

        List<Menu> menus = menuRepository.findAllById(menuIds);

        Map<UUID, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        return cartItems.stream()
                .map(cartItem -> {
                    Menu menu = menuMap.get(cartItem.menuId());
                    if (menu == null) throw new EntityNotFoundException(ErrorCode.MENU_NOT_FOUND);

                    return OrderItem.builder()
                            .menuName(cartItem.menuName())
                            .quantity(cartItem.quantity())
                            .price((int) cartItem.price())
                            .order(order)
                            .menu(menu)
                            .build();
                })
                .toList();
    }


    private List<Order> searchKeywords(List<Order> orders, List<String> keywords, boolean user) {
        List<Order> searchedOrders = new ArrayList<>();

        // 가게명 검색
//        List<String> stores = orders.stream().map(order -> order.getStore().getName()).toList();
//        List<String> matchedStores = stores.stream()
//                .filter(storeName -> keywords.stream().anyMatch(storeName::contains))
//                .toList();
        for (Order order : orders) {
            for (String store : keywords) {
                if (order.getStore().getName().contains(store)) {
                    if (!searchedOrders.contains(order)) {
                        searchedOrders.add(order);
                        break;
                    }
                }
            }

        }

        // 메뉴명 검색
//        List<String> menus = orders.stream()
//                .flatMap(order -> order.getOrderItems().stream())
//                .map(OrderItem::getMenuName)
//                .toList();
//        List<String> matchedMenus = menus.stream()
//                .filter(menuName -> keywords.stream().anyMatch(menuName::contains))
//                .toList();
        for (Order order : orders) {
            boolean menuMatched = false;
            for (OrderItem orderItem : order.getOrderItems()) {
                for (String menu : keywords) {
                    if (orderItem.getMenuName().contains(menu)) {
                        if (!searchedOrders.contains(order)) {
                            searchedOrders.add(order);
                        }
                        menuMatched = true;
                        break;
                    }
                }
                if (menuMatched) break;
            }
        }

        // 고객명 검색
        if (user) {
            for (Order order : orders) {
                for (String keyword :  keywords) {
                    if (order.getUser().getUsername().contains(keyword)) {
                        if (!searchedOrders.contains(order)) {
                            searchedOrders.add(order);
                            break;
                        }
                    }
                }
            }
        }

        return searchedOrders;
    }

    private List<String> validSearchKeywords(String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return List.of(keyword.split("\\s+"));
    }


}
