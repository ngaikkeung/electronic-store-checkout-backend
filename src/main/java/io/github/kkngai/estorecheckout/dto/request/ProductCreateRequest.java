package io.github.kkngai.estorecheckout.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateRequest {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String category;
}
