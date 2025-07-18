package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.dto.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ProductResponse;
import io.github.kkngai.estorecheckout.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProductDTOs(String category, BigDecimal priceMin, BigDecimal priceMax, Boolean inStock) {
        List<Product> products = productMapper.findAll(); // Simplified for now
        return products.stream()
                .map(ProductResponse::convertToProductResponse)
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }

    public Optional<ProductResponse> getProductDtoById(Long id) {
        return productMapper.findById(id)
                .map(ProductResponse::convertToProductResponse);
    }

    public Product getExistingProductById(Long id) {
        return productMapper.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + id));
    }

    public List<Product> createProducts(List<ProductCreateRequest> productCreateRequests) {
        List<Product> products = productCreateRequests.stream()
                .map(request -> {
                    Product product = new Product();
                    product.setName(request.getName());
                    product.setPrice(request.getPrice());
                    product.setStock(request.getStock());
                    product.setCategory(request.getCategory());
                    productMapper.insert(product);
                    return product;
                })
                .collect(Collectors.toList());
        return products;
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productMapper.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        productMapper.update(product);
        return product;
    }

    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }
} 