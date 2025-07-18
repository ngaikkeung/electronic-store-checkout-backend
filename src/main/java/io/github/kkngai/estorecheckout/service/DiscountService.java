package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.mapper.DiscountMapper;
import io.github.kkngai.estorecheckout.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountMapper discountMapper;
    private final ProductMapper productMapper;

    public void saveDiscount(Discount discount) {
        discountMapper.insert(discount);
    }

    public List<Discount> createDiscounts(List<DiscountCreateRequest> discountCreateRequests) {
        List<Discount> discounts = discountCreateRequests.stream()
                .map(request -> {
                    Discount discount = new Discount();
                    if (request.getProductId() != null) {
                        Product product = productMapper.findById(request.getProductId())
                                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + request.getProductId()));
                        discount.setProduct(product);
                    }
                    discount.setDescription(request.getDescription());
                    discount.setDiscountType(request.getDiscountType());
                    discount.setRules(request.getRules());
                    discount.setExpirationDate(request.getExpirationDate());
                    discountMapper.insert(discount);
                    return discount;
                })
                .collect(Collectors.toList());
        return discounts;
    }

    public Discount updateDiscount(Long discountId, Discount discountDetails) {
        Discount discount = discountMapper.findById(discountId)
                .orElseThrow(() -> new BusinessException(BusinessCode.DISCOUNT_NOT_FOUND, "Discount not found with id: " + discountId));
        discount.setProduct(discountDetails.getProduct());
        discount.setDescription(discountDetails.getDescription());
        discount.setDiscountType(discountDetails.getDiscountType());
        discount.setRules(discountDetails.getRules());
        discount.setExpirationDate(discountDetails.getExpirationDate());
        discountMapper.update(discount);
        return discount;
    }

    public List<Discount> getAllDiscounts(Pageable pageable) {
        return discountMapper.findAll();
    }
}
