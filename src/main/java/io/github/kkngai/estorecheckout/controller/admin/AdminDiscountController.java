package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<List<Discount>> createDiscounts(@RequestBody List<DiscountCreateRequest> discountCreateRequests) {
        return ResponseEntity.ok(discountService.createDiscounts(discountCreateRequests));
    }

    @PutMapping("/{discountId}")
    public ResponseEntity<Discount> updateDiscount(@PathVariable Long discountId, @RequestBody Discount discountDetails) {
        return ResponseEntity.ok(discountService.updateDiscount(discountId, discountDetails));
    }
}
