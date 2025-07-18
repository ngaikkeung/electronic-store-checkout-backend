package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.model.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.model.response.CustomPage;
import io.github.kkngai.estorecheckout.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<List<Product>> createProducts(@RequestBody List<ProductCreateRequest> productCreateRequests) {
        return ResponseEntity.ok(productService.createProducts(productCreateRequests));
    }

    @GetMapping
    public ResponseEntity<CustomPage<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
