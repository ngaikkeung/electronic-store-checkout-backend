package io.github.kkngai.estorecheckout.service.discount;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BundlePriceStrategy extends AbstractDiscountStrategy<BundlePriceStrategy.BundlePriceRules> {

    @Data
    protected static class BundlePriceRules {
        private Integer quantity;
        private BigDecimal amount;
    }
}
