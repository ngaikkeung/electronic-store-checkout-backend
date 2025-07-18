package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Additional query methods if needed
} 