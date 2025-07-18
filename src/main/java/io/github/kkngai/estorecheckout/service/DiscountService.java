package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.dto.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.repository.DiscountRepository;
import io.github.kkngai.estorecheckout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }

    public List<Discount> createDiscounts(List<DiscountCreateRequest> discountCreateRequests) {
        List<Discount> discounts = discountCreateRequests.stream()
                .map(request -> {
                    Discount discount = new Discount();
                    if (request.getProductId() != null) {
                        Product product = productRepository.findById(request.getProductId())
                                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + request.getProductId()));
                        discount.setProduct(product);
                    }
                    discount.setDescription(request.getDescription());
                    discount.setDiscountType(request.getDiscountType());
                    discount.setRules(request.getRules());
                    discount.setExpirationDate(request.getExpirationDate());
                    return discount;
                })
                .collect(Collectors.toList());
        return discountRepository.saveAll(discounts);
    }

    public Discount updateDiscount(Long discountId, Discount discountDetails) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BusinessException(BusinessCode.DISCOUNT_NOT_FOUND, "Discount not found with id: " + discountId));
        discount.setProduct(discountDetails.getProduct());
        discount.setDescription(discountDetails.getDescription());
        discount.setDiscountType(discountDetails.getDiscountType());
        discount.setRules(discountDetails.getRules());
        discount.setExpirationDate(discountDetails.getExpirationDate());
        return discountRepository.save(discount);
    }
}
