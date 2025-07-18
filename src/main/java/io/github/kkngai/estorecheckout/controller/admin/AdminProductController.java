package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.model.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.model.response.CustomPage;
import io.github.kkngai.estorecheckout.model.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public UnifiedResponse<List<Product>> createProducts(@RequestBody List<ProductCreateRequest> productCreateRequests) {
        return UnifiedResponse.success(productService.createProducts(productCreateRequests));
    }

    @GetMapping
    public UnifiedResponse<CustomPage<Product>> getAllProducts(Pageable pageable) {
        return UnifiedResponse.success(productService.getAllProducts(pageable));
    }

    @PutMapping("/{id}")
    public UnifiedResponse<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return UnifiedResponse.success(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public UnifiedResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return UnifiedResponse.success();
    }
}
