package io.github.kkngai.estorecheckout.dto.request;

import io.github.kkngai.estorecheckout.model.DiscountType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscountCreateRequest {
    private Long productId;
    private String description;
    private DiscountType discountType;
    private String rules;
    private LocalDateTime expirationDate;
}