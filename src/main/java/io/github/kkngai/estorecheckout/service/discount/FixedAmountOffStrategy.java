package io.github.kkngai.estorecheckout.service.discount;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FixedAmountOffStrategy extends AbstractDiscountStrategy<FixedAmountOffStrategy.FixedAmountOffRules> {

    @Data
    protected static class FixedAmountOffRules {
        private BigDecimal amount;
    }
}
