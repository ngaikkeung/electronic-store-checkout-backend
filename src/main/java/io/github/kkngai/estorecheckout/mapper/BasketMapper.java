package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Basket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface BasketMapper {
    Optional<Basket> findByUserId(@Param("userId") Long userId);
    void insert(Basket basket);
    void update(Basket basket);
    void deleteById(@Param("basketId") Long basketId);
}
