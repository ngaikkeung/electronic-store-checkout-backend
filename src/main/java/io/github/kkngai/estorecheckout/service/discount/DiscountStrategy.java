package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Discount;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateItemDiscount(BasketItem basketItem, Basket basket, Discount discount);
}
