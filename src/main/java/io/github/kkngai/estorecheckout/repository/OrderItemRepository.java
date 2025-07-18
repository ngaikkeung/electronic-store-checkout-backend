package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Additional query methods if needed
} 