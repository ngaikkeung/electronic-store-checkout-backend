package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.model.response.CustomPage;
import io.github.kkngai.estorecheckout.model.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public UnifiedResponse<CustomPage<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Boolean inStock,
            Pageable pageable) {
        return UnifiedResponse.success(productService.getAllProducts(category, priceMin, priceMax, inStock, pageable));
    }

    @GetMapping("/{id}")
    public UnifiedResponse<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(UnifiedResponse::success)
                .orElse(UnifiedResponse.error("404", "Product not found"));
    }
} 