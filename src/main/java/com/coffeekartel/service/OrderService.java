package com.coffeekartel.service;

import com.coffeekartel.dto.request.CreateOrderRequest;
import com.coffeekartel.dto.request.OrderItemRequest;
import com.coffeekartel.dto.response.OrderItemResponse;
import com.coffeekartel.dto.response.OrderResponse;
import com.coffeekartel.entity.MenuItem;
import com.coffeekartel.entity.Order;
import com.coffeekartel.entity.OrderItem;
import com.coffeekartel.entity.User;
import com.coffeekartel.exception.ResourceNotFoundException;
import com.coffeekartel.repository.MenuItemRepository;
import com.coffeekartel.repository.OrderRepository;
import com.coffeekartel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, MenuItemRepository menuItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String authenticatedUsername) {
        User user = null;
        if (authenticatedUsername != null) {
            user = userRepository.findByEmail(authenticatedUsername)
                    .or(() -> userRepository.findByPhone(authenticatedUsername))
                    .orElse(null);
        }

        String orderNumber = generateUniqueOrderNumber();

        Order order = new Order(
                orderNumber,
                user,
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getServiceType().toUpperCase(),
                BigDecimal.ZERO
        );
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTableNumber(request.getTableNumber());
        order.setSpecialNotes(request.getSpecialNotes());

        BigDecimal calculatedTotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem;
            if (itemReq.getMenuItemId() != null) {
                menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + itemReq.getMenuItemId()));
            } else if (itemReq.getSlug() != null && !itemReq.getSlug().trim().isEmpty()) {
                menuItem = menuItemRepository.findBySlug(itemReq.getSlug().trim())
                        .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with slug: " + itemReq.getSlug()));
            } else {
                throw new IllegalArgumentException("Menu item must specify either menuItemId or slug.");
            }

            if (!menuItem.isAvailable()) {
                throw new IllegalStateException("Item '" + menuItem.getName() + "' is currently sold out.");
            }

            BigDecimal unitPrice = menuItem.getPrice() != null ? menuItem.getPrice() : BigDecimal.ZERO;
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            calculatedTotal = calculatedTotal.add(subtotal);

            OrderItem orderItem = new OrderItem(
                    order,
                    menuItem,
                    unitPrice,
                    itemReq.getQuantity(),
                    subtotal
            );
            items.add(orderItem);
        }

        order.setItems(items);
        order.setTotalAmount(calculatedTotal);

        Order saved = orderRepository.save(order);
        log.info("Order {} created for customer {} with total amount ₹{}",
                saved.getOrderNumber(), saved.getCustomerName(), saved.getTotalAmount());

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public com.coffeekartel.dto.response.OrderTrackingResponse getPublicTrackingByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));

        List<com.coffeekartel.dto.response.OrderTrackingResponse.TrackingItem> trackingItems = order.getItems().stream()
                .map(item -> new com.coffeekartel.dto.response.OrderTrackingResponse.TrackingItem(
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new com.coffeekartel.dto.response.OrderTrackingResponse(
                order.getOrderNumber(),
                order.getServiceType(),
                order.getTableNumber(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                trackingItems
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForUser(String username) {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByPhone(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersAdmin() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus.toUpperCase());
        Order updated = orderRepository.save(order);
        log.info("Order {} status updated to {}", updated.getOrderNumber(), updated.getOrderStatus());
        return mapToResponse(updated);
    }

    private String generateUniqueOrderNumber() {
        String number;
        do {
            int randomNum = 1000 + RANDOM.nextInt(9000);
            number = "CK-ORD-" + randomNum;
        } while (orderRepository.findByOrderNumber(number).isPresent());
        return number;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getMenuItem() != null ? item.getMenuItem().getId() : null,
                        item.getMenuItem() != null ? item.getMenuItem().getName() : "Item",
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getServiceType(),
                order.getDeliveryAddress(),
                order.getTableNumber(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getSpecialNotes(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}
