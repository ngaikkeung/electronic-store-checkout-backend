package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.dto.response.ProductResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public CustomPage<ProductResponse> getAllProductDTOs(String category, BigDecimal priceMin, BigDecimal priceMax, Boolean inStock, Pageable pageable) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category));
        }
        if (priceMin != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin));
        }
        if (priceMax != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax));
        }
        if (inStock != null) {
            if (inStock) {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stock"), 0));
            } else {
                spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("stock"), 0));
            }
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        List<ProductResponse> content = page.getContent().stream()
                .map(ProductResponse::convertToProductResponse)
                .collect(Collectors.toList());
        return new CustomPage<>(content, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public CustomPage<Product> getAllProducts(Pageable pageable) {
        return new CustomPage<>(productRepository.findAll(pageable));
    }

    public Optional<ProductResponse> getProductDtoById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::convertToProductResponse);
    }

    public Product getExistingProductById(Long id) {
        return productRepository.findById(id)
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
                    return product;
                })
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
} 