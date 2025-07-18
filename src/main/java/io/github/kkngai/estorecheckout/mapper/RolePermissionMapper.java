package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RolePermissionMapper {
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
    List<RolePermission> findByRoleId(@Param("roleId") Integer roleId);
    List<RolePermission> findByPermissionId(@Param("permissionId") Integer permissionId);
    void insert(RolePermission rolePermission);
    void delete(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);
}
