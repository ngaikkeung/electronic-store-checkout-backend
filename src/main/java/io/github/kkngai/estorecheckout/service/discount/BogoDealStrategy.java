package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Discount;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class BogoDealStrategy extends AbstractDiscountStrategy<BogoDealStrategy.BogoDealRules> {

    @Override
    public BigDecimal calculateItemDiscount(BasketItem basketItem, Basket basket, Discount discount) {
        BogoDealRules bogoDealRules = getRules(discount, BogoDealRules.class);
        int buyQuantity = bogoDealRules.getBuyQuantity();
        int getQuantity = bogoDealRules.getGetQuantity();
        BigDecimal discountPercentage = bogoDealRules.getDiscountPercentage();

        BigDecimal originalPricePerUnit = basketItem.getProduct().getPrice();
        int itemQuantity = basketItem.getQuantity();

        int numSets = itemQuantity / (buyQuantity + getQuantity);
        int discountedUnits = numSets * getQuantity;

        BigDecimal totalFullPriceUnits = originalPricePerUnit.multiply(BigDecimal.valueOf(itemQuantity - discountedUnits));
        BigDecimal totalDiscountedPriceUnits = originalPricePerUnit
                .multiply(BigDecimal.valueOf(discountedUnits))
                .multiply(BigDecimal.ONE.subtract(discountPercentage.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));

        BigDecimal totalPrice = totalFullPriceUnits.add(totalDiscountedPriceUnits);
        return totalPrice.divide(BigDecimal.valueOf(itemQuantity), MathContext.DECIMAL128);
    }

    @Data
    protected static class BogoDealRules {
        private Integer buyQuantity;
        private Integer getQuantity;
        private BigDecimal discountPercentage;
    }
}
