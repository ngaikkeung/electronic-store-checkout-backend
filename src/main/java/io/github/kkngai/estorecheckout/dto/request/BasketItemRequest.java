package io.github.kkngai.estorecheckout.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemRequest {
    private Long productId;
    private Integer quantity;
}
