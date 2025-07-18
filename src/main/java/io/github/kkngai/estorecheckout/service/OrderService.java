package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.repository.OrderItemRepository;
import io.github.kkngai.estorecheckout.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BasketService basketService;
    private final UserService userService;

    public CustomPage<Order> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return new CustomPage<>(page);
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
        order = orderRepository.save(order);

        // Move basket items to order items
        for (var basketItem : basket.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(basketItem.getProduct());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPriceAtPurchase(basketItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        // Clear the basket after order creation
        basket.getItems().clear();
        basketService.saveBasket(basket);

        return order;
    }

    public CustomPage<Order> getUserOrders(Long userId, Pageable pageable) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));
        Page<Order> page = orderRepository.findByUser(user, pageable);
        return new CustomPage<>(page);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    // This method would be more complex in a real scenario, involving discount calculations
    public Order getOrderReceipt(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessCode.ORDER_NOT_FOUND, "Order not found"));
        // In a real application, you would fetch order items, apply discounts, etc.
        // For now, we'll just return the order with its items (if fetched eagerly or through a separate call)
        return order;
    }
}
