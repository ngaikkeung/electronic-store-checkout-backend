package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

public class SpendThresholdStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        SpendThresholdRules spendThresholdRules = objectMapper.convertValue(rules, SpendThresholdRules.class);
        BigDecimal spendThreshold = spendThresholdRules.getSpendThreshold();
        BigDecimal amount = spendThresholdRules.getAmount();

        if (basket.getTotalPrice().compareTo(spendThreshold) >= 0) {
            return amount;
        }
        return BigDecimal.ZERO;
    }

    @Data
    private static class SpendThresholdRules {
        private BigDecimal spendThreshold;
        private BigDecimal amount;
    }
}
