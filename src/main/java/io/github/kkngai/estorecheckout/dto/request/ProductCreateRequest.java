package io.github.kkngai.estorecheckout.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String category;
}
