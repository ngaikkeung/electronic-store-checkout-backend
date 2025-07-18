package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;

import java.math.BigDecimal;
import java.util.Map;

public interface DiscountStrategy {
    BigDecimal apply(Basket basket, Map<String, Object> rules);
}
