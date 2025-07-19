package io.github.kkngai.estorecheckout.service.discount;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Discount;

import java.math.BigDecimal;

public abstract class AbstractDiscountStrategy<T> implements DiscountStrategy {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T getRules(Discount discount, Class<T> valueType) {
        try {
            return objectMapper.readValue(discount.getRules(), valueType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse discount rules", e);
        }
    }

    @Override
    public BigDecimal calculateItemDiscount(BasketItem basketItem, Basket basket, Discount discount) {
        return basketItem.getProduct().getPrice();
    }
}
