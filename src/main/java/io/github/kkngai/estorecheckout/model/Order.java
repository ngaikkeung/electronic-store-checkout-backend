package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long orderId;
    private User user;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
} 