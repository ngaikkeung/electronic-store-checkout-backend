package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.repository.BasketItemRepository;
import io.github.kkngai.estorecheckout.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final ProductService productService;
    private final UserService userService;
    private final StockService stockService;
    private final RedisTemplate<String, Basket> basketRedisTemplate;

    private String getBasketKey(Long userId) {
        return "basket:" + userId;
    }

    @Transactional
    public Basket getOrCreateBasket(Long userId) {
        Basket basket = basketRedisTemplate.opsForValue().get(getBasketKey(userId));
        if (basket != null) {
            return basket;
        }

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
        log.info("Adding product: {} to basket for user: {}", productId, userId);
        if (!stockService.reserveStock(productId, quantity)) {
            throw new BusinessException(BusinessCode.PRODUCT_OUT_OF_STOCK, "Product with id: " + productId + " is out of stock or requested quantity is not available.");
        }

        try {
            Basket basket = getOrCreateBasket(userId);
            Product product = productService.getExistingProductById(productId);

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

            basketRepository.save(basket);
            stockService.persistStockUpdate(productId, -quantity);
            product = productService.getExistingProductById(productId);
            basketItem.setProduct(product);
            basketRedisTemplate.opsForValue().set(getBasketKey(userId), basket, Duration.ofMinutes(30));
            return basketItem;
        } catch (Exception e) {
            stockService.rollbackStock(productId, quantity);
            log.error("Failed to add product: {} to basket, {}", productId, e.getMessage());
            throw new BusinessException(BusinessCode.SYSTEM_ERROR, "Failed to add product to basket, " + e.getMessage());
        }
    }

    @Transactional
    public void removeBasketItem(Long userId, Long basketItemId, int quantityToRemove) {
        Basket basket = getOrCreateBasket(userId);
        BasketItem itemToRemove = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> new BusinessException(BusinessCode.BASKET_ITEM_NOT_FOUND, "Basket item not found"));

        if (!itemToRemove.getBasket().getBasketId().equals(basket.getBasketId())) {
            throw new BusinessException(BusinessCode.BASKET_ITEM_MISMATCH, "Basket item does not belong to the user's basket");
        }

        int currentQuantity = itemToRemove.getQuantity();
        Long productId = itemToRemove.getProduct().getProductId();

        if (quantityToRemove >= currentQuantity) {
            basket.getItems().remove(itemToRemove);
            basketItemRepository.delete(itemToRemove);
            stockService.persistStockUpdate(productId, currentQuantity);
        } else {
            itemToRemove.setQuantity(currentQuantity - quantityToRemove);
            basketItemRepository.save(itemToRemove);
            stockService.persistStockUpdate(productId, quantityToRemove);
        }

        Product updatedProduct = productService.getExistingProductById(productId);
        itemToRemove.setProduct(updatedProduct);

        basketRepository.save(basket);
        basketRedisTemplate.opsForValue().set(getBasketKey(userId), basket, Duration.ofMinutes(30));
    }

    public Basket saveBasket(Basket basket) {
        return basketRepository.save(basket);
    }
}
