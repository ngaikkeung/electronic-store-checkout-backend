package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.UserRole;
import io.github.kkngai.estorecheckout.model.UserRole.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    // Additional query methods if needed
} 