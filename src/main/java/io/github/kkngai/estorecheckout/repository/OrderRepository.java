package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Additional query methods if needed
} 