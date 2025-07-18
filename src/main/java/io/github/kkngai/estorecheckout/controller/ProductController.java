package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ProductResponse;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
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
    public UnifiedResponse<CustomPage<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Boolean inStock,
            Pageable pageable) {
        return UnifiedResponse.success(productService.getAllProductDTOs(category, priceMin, priceMax, inStock, pageable));
    }

    @GetMapping("/{id}")
    public UnifiedResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductDtoById(id)
                .map(UnifiedResponse::success)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + id));
    }
} 