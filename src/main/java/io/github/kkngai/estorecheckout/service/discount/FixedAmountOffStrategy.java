package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;

import java.math.BigDecimal;
import java.util.Map;

public class FixedAmountOffStrategy implements DiscountStrategy {
    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        // TODO: Implement actual fixed amount off logic based on basket and rules
        return BigDecimal.ZERO;
    }
}
