package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByExpirationDateIsNullOrCreateAtBeforeAndExpirationDateAfter(LocalDateTime now1, LocalDateTime now2);
}