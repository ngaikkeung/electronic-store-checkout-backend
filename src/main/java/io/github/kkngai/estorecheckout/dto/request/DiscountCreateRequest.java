package io.github.kkngai.estorecheckout.dto.request;

import io.github.kkngai.estorecheckout.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCreateRequest {
    private Long productId;
    private String description;
    private DiscountType discountType;
    private String rules;
    private LocalDateTime expirationDate;
}
