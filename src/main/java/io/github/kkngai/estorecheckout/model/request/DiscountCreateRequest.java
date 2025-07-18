package io.github.kkngai.estorecheckout.model.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DiscountCreateRequest {
    private Long productId;
    private String description;
    private String discountType;
    private String rules;
    private LocalDateTime expirationDate;
}