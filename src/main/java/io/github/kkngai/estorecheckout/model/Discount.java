package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    private Long discountId;
    private Long productId;
    private Product product;
    private String description;
    private DiscountType discountType;
    private String rules;
    private LocalDateTime expirationDate;
} 