package io.github.kkngai.estorecheckout.service.discount;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PercentageDiscountStrategy extends AbstractDiscountStrategy<PercentageDiscountStrategy.PercentageRules> {

    @Data
    protected static class PercentageRules {
        private BigDecimal percentage;
    }
}
