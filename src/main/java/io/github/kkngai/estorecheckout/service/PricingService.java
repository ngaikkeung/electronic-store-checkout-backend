package io.github.kkngai.estorecheckout.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.DiscountType;
import io.github.kkngai.estorecheckout.service.discount.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final Map<DiscountType, DiscountStrategy> discountStrategies;
    private final ObjectMapper objectMapper;

    public PricingService() {
        this.discountStrategies = new HashMap<>();
        discountStrategies.put(DiscountType.PERCENTAGE, new PercentageDiscountStrategy());
        discountStrategies.put(DiscountType.BOGO_DEAL, new BogoDealStrategy());
        discountStrategies.put(DiscountType.FIXED_AMOUNT_OFF, new FixedAmountOffStrategy());
        discountStrategies.put(DiscountType.SPEND_THRESHOLD, new SpendThresholdStrategy());
        discountStrategies.put(DiscountType.BUNDLE_PRICE, new BundlePriceStrategy());
        this.objectMapper = new ObjectMapper();
    }

    public BigDecimal calculateDiscount(Basket basket, Discount discount) {
        DiscountStrategy strategy = discountStrategies.get(discount.getDiscountType());
        if (strategy != null) {
            try {
                Map<String, Object> rulesMap = objectMapper.readValue(discount.getRules(), new TypeReference<>() {
                });
                return strategy.apply(basket, rulesMap);
            } catch (JsonProcessingException e) {
                throw new BusinessException(BusinessCode.INVALID_DISCOUNT_RULES, "Invalid discount rules JSON: " + e.getMessage());
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateTotalAmount(Basket basket, List<Discount> applicableDiscounts) {
        BigDecimal total = basket.getTotalPrice();
        for (Discount discount : applicableDiscounts) {
            total = total.subtract(calculateDiscount(basket, discount));
        }
        return total;
    }
}
