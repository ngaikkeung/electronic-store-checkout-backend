package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.BasketItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface BasketItemMapper {
    Optional<BasketItem> findById(@Param("basketItemId") Long basketItemId);
    void insert(BasketItem basketItem);
    void update(BasketItem basketItem);
    void deleteById(@Param("basketItemId") Long basketItemId);
}
