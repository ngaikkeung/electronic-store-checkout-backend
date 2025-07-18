package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    // Additional query methods if needed
} 