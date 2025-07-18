package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PermissionMapper {
    Optional<Permission> findById(@Param("permissionId") Integer permissionId);
    Optional<Permission> findByPermissionName(@Param("permissionName") String permissionName);
    void insert(Permission permission);
    void update(Permission permission);
    void deleteById(@Param("permissionId") Integer permissionId);
}
