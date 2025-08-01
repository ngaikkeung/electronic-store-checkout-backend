package io.github.kkngai.estorecheckout.repository;

import io.github.kkngai.estorecheckout.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByStockGreaterThan(Integer quantity);
} 