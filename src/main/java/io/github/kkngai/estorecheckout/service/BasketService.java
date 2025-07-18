package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.User;
import io.github.kkngai.estorecheckout.repository.BasketItemRepository;
import io.github.kkngai.estorecheckout.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional
    public Basket getOrCreateBasket(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));
        return basketRepository.findByUser(user)
                .orElseGet(() -> {
                    Basket newBasket = new Basket();
                    newBasket.setUser(user);
                    return basketRepository.save(newBasket);
                });
    }

    @Transactional
    public BasketItem addProductToBasket(Long userId, Long productId, int quantity) {
        Basket basket = getOrCreateBasket(userId);
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"));

        Optional<BasketItem> existingItem = basket.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        BasketItem basketItem;
        if (existingItem.isPresent()) {
            basketItem = existingItem.get();
            basketItem.setQuantity(basketItem.getQuantity() + quantity);
        } else {
            basketItem = new BasketItem();
            basketItem.setBasket(basket);
            basketItem.setProduct(product);
            basketItem.setQuantity(quantity);
            basket.getItems().add(basketItem);
        }
        basketRepository.save(basket); // Save basket to cascade changes to items
        return basketItemRepository.save(basketItem);
    }

    @Transactional
    public void removeBasketItem(Long userId, Long basketItemId) {
        Basket basket = getOrCreateBasket(userId);
        BasketItem itemToRemove = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> new BusinessException(BusinessCode.BASKET_ITEM_NOT_FOUND, "Basket item not found"));

        if (!itemToRemove.getBasket().getBasketId().equals(basket.getBasketId())) {
            throw new BusinessException(BusinessCode.BASKET_ITEM_MISMATCH, "Basket item does not belong to the user's basket");
        }

        basket.getItems().remove(itemToRemove);
        basketItemRepository.delete(itemToRemove);
        basketRepository.save(basket); // Update basket to reflect removal
    }

    public Basket saveBasket(Basket basket) {
        return basketRepository.save(basket);
    }
}
