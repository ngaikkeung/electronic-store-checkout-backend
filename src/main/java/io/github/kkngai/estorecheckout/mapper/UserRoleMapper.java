package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRoleMapper {
    Optional<UserRole> findByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Integer roleId);
    List<UserRole> findByUserId(@Param("userId") Long userId);
    List<UserRole> findByRoleId(@Param("roleId") Integer roleId);
    void insert(UserRole userRole);
    void delete(@Param("userId") Long userId, @Param("roleId") Integer roleId);
}
