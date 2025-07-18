package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.model.response.CustomPage;
import io.github.kkngai.estorecheckout.model.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public UnifiedResponse<Order> createOrder() {
        Order order = orderService.createOrderFromBasket(getCurrentUserId());
        return UnifiedResponse.success(order);
    }

    @GetMapping
    public UnifiedResponse<CustomPage<Order>> getUserOrders(Pageable pageable) {
        return UnifiedResponse.success(orderService.getUserOrders(getCurrentUserId(), pageable));
    }

    @GetMapping("/{orderId}")
    public UnifiedResponse<Order> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(UnifiedResponse::success)
                .orElse(UnifiedResponse.error("404", "Order not found"));
    }

    @GetMapping("/{orderId}/receipt")
    public UnifiedResponse<Order> getOrderReceipt(@PathVariable Long orderId) {
        return UnifiedResponse.success(orderService.getOrderReceipt(orderId));
    }
}
