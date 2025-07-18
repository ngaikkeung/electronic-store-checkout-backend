package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    // Additional query methods if needed
} 