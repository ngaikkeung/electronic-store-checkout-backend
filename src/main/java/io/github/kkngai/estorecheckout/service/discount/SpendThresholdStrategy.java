package io.github.kkngai.estorecheckout.service.discount;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SpendThresholdStrategy extends AbstractDiscountStrategy<SpendThresholdStrategy.SpendThresholdRules> {
    @Data
    protected static class SpendThresholdRules {
        private BigDecimal spendThreshold;
        private BigDecimal amount;
    }
}
