package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByUser(User user);
}
