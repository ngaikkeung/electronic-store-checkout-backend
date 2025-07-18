package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface RoleMapper {
    Optional<Role> findById(@Param("roleId") Integer roleId);
    Optional<Role> findByRoleName(@Param("roleName") String roleName);
    void insert(Role role);
    void update(Role role);
    void deleteById(@Param("roleId") Integer roleId);
}
