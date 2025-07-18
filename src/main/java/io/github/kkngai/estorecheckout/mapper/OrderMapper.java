package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    Optional<Order> findById(@Param("orderId") Long orderId);
    List<Order> findAll();
    List<Order> findByUserId(@Param("userId") Long userId);
    void insert(Order order);
    void update(Order order);
    void deleteById(@Param("orderId") Long orderId);
}
