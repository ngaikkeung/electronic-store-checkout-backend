package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ReceiptResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.repository.OrderItemRepository;
import io.github.kkngai.estorecheckout.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BasketService basketService;
    private final UserService userService;
    private final PricingService pricingService;
    private final DiscountService discountService;

    public CustomPage<Order> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return new CustomPage<>(page);
    }

    @Transactional
    public Order createOrderFromBasket(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));

        Basket basket = basketService.getOrCreateBasket(userId);

        if (basket.getItems().isEmpty()) {
            throw new BusinessException(BusinessCode.EMPTY_BASKET, "Cannot create order from an empty basket");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PROCESSING);
        order.setCreatedAt(LocalDateTime.now());

        List<Discount> activeDiscounts = discountService.getAllActiveDiscounts();
        BigDecimal finalPrice = pricingService.calculateTotalAmount(basket, activeDiscounts);
        order.setTotalPrice(finalPrice);
        order = orderRepository.save(order);

        for (var basketItem : basket.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(basketItem.getProduct());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPriceAtPurchase(basketItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);
        }

        basket.getItems().clear();
        basketService.saveBasketAndCache(basket);

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

    public ReceiptResponse getOrderReceipt(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessCode.ORDER_NOT_FOUND, "Order not found"));

        BigDecimal subtotal = order.getOrderItems().stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountAmount = subtotal.subtract(order.getTotalPrice());

        return ReceiptResponse.fromOrder(order, discountAmount);
    }
}
