package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public UnifiedResponse<CustomPage<Order>> getAllOrders(Pageable pageable) {
        return UnifiedResponse.success(orderService.getAllOrders(pageable));
    }
}
