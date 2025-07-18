package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketItem {
    private Long basketItemId;
    private Long basketId;
    private Long productId;
    private Basket basket;
    private Product product;
    private Integer quantity;
}
