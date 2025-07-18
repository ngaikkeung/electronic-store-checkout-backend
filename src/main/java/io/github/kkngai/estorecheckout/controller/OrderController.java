package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.model.response.CustomPage;
import io.github.kkngai.estorecheckout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Assuming user ID can be extracted from security context or passed as a header/param for now
    private Long getCurrentUserId() {
        // This should be replaced with actual user ID retrieval from security context
        return 1L; // Placeholder for a logged-in user
    }

    @PostMapping
    public ResponseEntity<Order> createOrder() {
        Order order = orderService.createOrderFromBasket(getCurrentUserId());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<CustomPage<Order>> getUserOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(getCurrentUserId(), pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{orderId}/receipt")
    public ResponseEntity<Order> getOrderReceipt(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderReceipt(orderId));
    }
}
