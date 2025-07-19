package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.dto.request.BasketItemRequest;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    private Long getCurrentUserId() {
        return 1L;
    }

    @PostMapping("/items")
    public UnifiedResponse<BasketItem> addProductToBasket(@RequestBody BasketItemRequest request) {
        return UnifiedResponse.success(basketService.addProductToBasket(getCurrentUserId(), request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public UnifiedResponse<String> removeBasketItem(@PathVariable Long itemId, @RequestParam(required = false) Integer quantity) {
        int quantityToRemove = (quantity != null) ? quantity : Integer.MAX_VALUE;
        basketService.removeBasketItem(getCurrentUserId(), itemId, quantityToRemove);
        return UnifiedResponse.success("Item removed from basket successfully");
    }

    @GetMapping
    public UnifiedResponse<Basket> getBasket() {
        return UnifiedResponse.success(basketService.getOrCreateBasket(getCurrentUserId()));
    }
}
