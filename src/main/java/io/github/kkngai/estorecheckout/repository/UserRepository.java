package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods if needed
} 