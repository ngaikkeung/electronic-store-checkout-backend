package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Discount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DiscountMapper {
    Optional<Discount> findById(@Param("discountId") Long discountId);
    List<Discount> findAll();
    void insert(Discount discount);
    void update(Discount discount);
    void deleteById(@Param("discountId") Long discountId);
}
