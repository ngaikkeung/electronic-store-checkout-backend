package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private Order order;
    private Product product;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
} 