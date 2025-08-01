package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}