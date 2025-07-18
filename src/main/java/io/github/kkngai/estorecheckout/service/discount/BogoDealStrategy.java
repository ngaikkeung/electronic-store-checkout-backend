package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BogoDealStrategy implements DiscountStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BigDecimal apply(Basket basket, Map<String, Object> rules) {
        BogoDealRules bogoDealRules = objectMapper.convertValue(rules, BogoDealRules.class);
        int buyQuantity = bogoDealRules.getBuyQuantity();
        int getQuantity = bogoDealRules.getGetQuantity();
        BigDecimal discountPercentage = bogoDealRules.getDiscountPercentage();

        List<BasketItem> allItems = basket.getItems().stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getPrice()))
                .collect(Collectors.toList());

        int totalItems = allItems.stream().mapToInt(BasketItem::getQuantity).sum();

        if (totalItems < buyQuantity + getQuantity) {
            return BigDecimal.ZERO;
        }

        int numSets = totalItems / (buyQuantity + getQuantity);
        int freeItemsCount = numSets * getQuantity;

        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (int i = 0; i < freeItemsCount && i < allItems.size(); i++) {
            BasketItem item = allItems.get(i);
            totalDiscount = totalDiscount.add(item.getProduct().getPrice()
                    .multiply(discountPercentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
        }

        return totalDiscount;
    }

    @Data
    private static class BogoDealRules {
        private Integer buyQuantity;
        private Integer getQuantity;
        private BigDecimal discountPercentage;
    }
}
