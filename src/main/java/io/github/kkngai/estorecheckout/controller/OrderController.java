package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ReceiptResponse;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private Long getCurrentUserId() {
        return 1L;
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
                .orElseThrow(() -> new BusinessException(BusinessCode.ORDER_NOT_FOUND, "Order not found"));
    }

    @GetMapping("/{orderId}/receipt")
    public UnifiedResponse<ReceiptResponse> getOrderReceipt(@PathVariable Long orderId) {
        return UnifiedResponse.success(orderService.getOrderReceipt(orderId));
    }
}
