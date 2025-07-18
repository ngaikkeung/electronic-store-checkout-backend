package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderItemMapper {
    Optional<OrderItem> findById(@Param("orderItemId") Long orderItemId);
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    void insert(OrderItem orderItem);
    void update(OrderItem orderItem);
    void deleteById(@Param("orderItemId") Long orderItemId);
}
