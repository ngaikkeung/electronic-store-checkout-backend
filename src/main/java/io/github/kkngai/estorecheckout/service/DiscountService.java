package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    public List<Discount> saveAllDiscounts(List<Discount> discounts) {
        return discountRepository.saveAll(discounts);
    }

    public Discount updateDiscount(Long discountId, Discount discountDetails) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new RuntimeException("Discount not found with id: " + discountId));
        discount.setProduct(discountDetails.getProduct());
        discount.setDescription(discountDetails.getDescription());
        discount.setDiscountType(discountDetails.getDiscountType());
        discount.setRules(discountDetails.getRules());
        discount.setExpirationDate(discountDetails.getExpirationDate());
        return discountRepository.save(discount);
    }
}
