package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.mapper.OrderItemMapper;
import io.github.kkngai.estorecheckout.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final BasketService basketService;
    private final UserService userService;
    private final PricingService pricingService;

    public List<Order> getAllOrders(Pageable pageable) {
        return orderMapper.findAll();
    }

    @Transactional
    public Order createOrderFromBasket(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));

        // Get the user's basket
        Basket basket = basketService.getOrCreateBasket(userId);

        if (basket.getItems().isEmpty()) {
            throw new BusinessException(BusinessCode.EMPTY_BASKET, "Cannot create order from an empty basket");
        }

        // Create a new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PROCESSING");
        order.setTotalPrice(basket.getTotalPrice());
        order.setCreatedAt(LocalDateTime.now());
        orderMapper.insert(order);

        // Move basket items to order items
        for (var basketItem : basket.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(basketItem.getProduct());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPriceAtPurchase(basketItem.getProduct().getPrice());
            orderItemMapper.insert(orderItem);
        }

        // Clear the basket after order creation
        basket.getItems().clear();
        basketService.saveBasket(basket);

        return order;
    }

    public List<Order> getUserOrders(Long userId, Pageable pageable) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));
        return orderMapper.findByUserId(userId);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderMapper.findById(orderId);
    }

    // This method would be more complex in a real scenario, involving discount calculations
    public Order getOrderReceipt(Long orderId) {
        Order order = orderMapper.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessCode.ORDER_NOT_FOUND, "Order not found"));
        // In a real application, you would fetch order items, apply discounts, etc.
        // For now, we'll just return the order with its items (if fetched eagerly or through a separate call)
        return order;
    }
}
