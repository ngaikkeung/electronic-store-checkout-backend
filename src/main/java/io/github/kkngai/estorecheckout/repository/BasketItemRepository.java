package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
}
