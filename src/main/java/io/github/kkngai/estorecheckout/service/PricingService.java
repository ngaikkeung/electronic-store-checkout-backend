package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.DiscountType;
import io.github.kkngai.estorecheckout.service.discount.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingService {

    private final Map<DiscountType, DiscountStrategy> discountStrategyMap = new HashMap<>();
    private final DiscountService discountService;

    public PricingService(DiscountService discountService,
                         PercentageDiscountStrategy percentageDiscountStrategy,
                         FixedAmountOffStrategy fixedAmountOffStrategy,
                         SpendThresholdStrategy spendThresholdStrategy,
                         BundlePriceStrategy bundlePriceStrategy,
                         BogoDealStrategy bogoDealStrategy) {
        this.discountService = discountService;
        discountStrategyMap.put(DiscountType.PERCENTAGE, percentageDiscountStrategy);
        discountStrategyMap.put(DiscountType.FIXED_AMOUNT_OFF, fixedAmountOffStrategy);
        discountStrategyMap.put(DiscountType.SPEND_THRESHOLD, spendThresholdStrategy);
        discountStrategyMap.put(DiscountType.BUNDLE_PRICE, bundlePriceStrategy);
        discountStrategyMap.put(DiscountType.BOGO_DEAL, bogoDealStrategy);
    }

    public BigDecimal calculateItemPriceWithDiscount(BasketItem currentItem, Basket basket) {
        BigDecimal finalPricePerUnit = currentItem.getProduct().getPrice();

        List<Discount> activeDiscounts = discountService.getAllActiveDiscountsByProductId(LocalDateTime.now(), currentItem.getProduct().getProductId());

        for (Discount discount : activeDiscounts) {
            DiscountStrategy strategy = discountStrategyMap.get(discount.getDiscountType());
            if (strategy != null) {
                finalPricePerUnit = strategy.calculateItemDiscount(currentItem, basket, discount);
            }
        }
        return finalPricePerUnit;
    }
}
