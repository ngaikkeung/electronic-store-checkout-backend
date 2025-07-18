package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

public class FixedAmountOffStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        FixedAmountOffRules fixedAmountOffRules = objectMapper.convertValue(rules, FixedAmountOffRules.class);
        BigDecimal amount = fixedAmountOffRules.getAmount();
        return amount;
    }

    @Data
    private static class FixedAmountOffRules {
        private BigDecimal amount;
    }
}
