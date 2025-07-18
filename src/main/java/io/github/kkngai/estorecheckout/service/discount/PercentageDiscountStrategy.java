package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class PercentageDiscountStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        PercentageRules percentageRules = objectMapper.convertValue(rules, PercentageRules.class);
        BigDecimal discountPercentage = percentageRules.getPercentage();
        BigDecimal totalDiscount = basket.getTotalPrice().multiply(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return totalDiscount;
    }

    @Data
    private static class PercentageRules {
        private BigDecimal percentage;
    }
}
