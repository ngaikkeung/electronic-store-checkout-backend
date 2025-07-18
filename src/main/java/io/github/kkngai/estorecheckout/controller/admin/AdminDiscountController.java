package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    @PostMapping
    public UnifiedResponse<List<Discount>> createDiscounts(@RequestBody List<DiscountCreateRequest> discountCreateRequests) {
        return UnifiedResponse.success(discountService.createDiscounts(discountCreateRequests));
    }

    @PutMapping("/{discountId}")
    public UnifiedResponse<Discount> updateDiscount(@PathVariable Long discountId, @RequestBody Discount discountDetails) {
        return UnifiedResponse.success(discountService.updateDiscount(discountId, discountDetails));
    }

    @GetMapping
    public UnifiedResponse<CustomPage<Discount>> getAllDiscounts(Pageable pageable) {
        return UnifiedResponse.success(discountService.getAllDiscounts(pageable));
    }
}
