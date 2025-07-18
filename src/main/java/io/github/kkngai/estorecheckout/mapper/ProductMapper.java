package io.github.kkngai.estorecheckout.mapper;

import io.github.kkngai.estorecheckout.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    Optional<Product> findById(@Param("productId") Long productId);
    List<Product> findAll();
    void insert(Product product);
    void update(Product product);
    void deleteById(@Param("productId") Long productId);
}
