package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

public class BundlePriceStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        BundlePriceRules bundlePriceRules = objectMapper.convertValue(rules, BundlePriceRules.class);
        int quantity = bundlePriceRules.getQuantity();
        BigDecimal amount = bundlePriceRules.getAmount();

        int totalBasketQuantity = basket.getItems().stream()
                .mapToInt(BasketItem::getQuantity)
                .sum();

        if (totalBasketQuantity >= quantity) {
            return amount;
        }
        return BigDecimal.ZERO;
    }

    @Data
    private static class BundlePriceRules {
        private Integer quantity;
        private BigDecimal amount;
    }
}
