package com.coffeekartel.controller;

import com.coffeekartel.dto.request.CreateOrderRequest;
import com.coffeekartel.dto.request.UpdateStatusRequest;
import com.coffeekartel.dto.response.OrderResponse;
import com.coffeekartel.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Orders", description = "Endpoints for placing and tracking customer orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @Operation(summary = "Place a new order", description = "Submit dine-in, drive-thru, or delivery order")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
                ? authentication.getName()
                : null;

        OrderResponse response = orderService.createOrder(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders/{orderNumber}")
    @Operation(summary = "Track order by order number", description = "Lookup current status of an order using reference number")
    public ResponseEntity<com.coffeekartel.dto.response.OrderTrackingResponse> trackOrder(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.getPublicTrackingByNumber(orderNumber));
    }

    @GetMapping("/orders/my-orders")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "User order history", description = "Retrieve list of previous orders for the logged-in customer")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrdersForUser(authentication.getName()));
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "All orders (Admin)", description = "View all orders for kitchen display or staff dispatch")
    public ResponseEntity<List<OrderResponse>> getAllOrdersAdmin() {
        return ResponseEntity.ok(orderService.getAllOrdersAdmin());
    }

    @PatchMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (Admin)", description = "Change order status (RECEIVED, PREPARING, READY, COMPLETED, CANCELLED)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }
}
