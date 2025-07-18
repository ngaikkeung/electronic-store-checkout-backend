package io.github.kkngai.estorecheckout.model.response;

import io.github.kkngai.estorecheckout.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    private String category;
    private boolean inStock;

    public static ProductResponse convertToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setInStock(product.getStock() > 0);
        return response;
    }
}
