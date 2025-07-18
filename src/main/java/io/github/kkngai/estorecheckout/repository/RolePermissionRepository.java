package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.RolePermission;
import io.github.kkngai.estorecheckout.model.RolePermission.RolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermissionId> {
    // Additional query methods if needed
} 