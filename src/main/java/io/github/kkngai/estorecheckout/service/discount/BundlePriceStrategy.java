package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;

import java.math.BigDecimal;
import java.util.Map;

public class BundlePriceStrategy implements DiscountStrategy {
    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        // TODO: Implement actual bundle price logic based on basket and rules
        return BigDecimal.ZERO;
    }
}
