package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(@Param("userId") Long userId);
    Optional<User> findByEmail(@Param("email") String email);
    void insert(User user);
    void update(User user);
    void deleteById(@Param("userId") Long userId);
}
