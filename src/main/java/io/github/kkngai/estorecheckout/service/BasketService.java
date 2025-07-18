package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.mapper.BasketItemMapper;
import io.github.kkngai.estorecheckout.mapper.BasketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final BasketMapper basketMapper;
    private final BasketItemMapper basketItemMapper;
    private final ProductService productService;
    private final UserService userService;
    private final StockService stockService;
    private final RedisTemplate<String, Basket> basketRedisTemplate;

    private String getBasketKey(Long userId) {
        return "basket:" + userId;
    }

    @Transactional
    public Basket getOrCreateBasket(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));
        Optional<Basket> existingBasket = basketMapper.findByUserId(userId);
        if (existingBasket.isPresent()) {
            return existingBasket.get();
        } else {
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            newBasket.setCreatedAt(LocalDateTime.now());
            newBasket.setUpdatedAt(LocalDateTime.now());
            basketMapper.insert(newBasket);
            return newBasket;
        }
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
                basketItemMapper.update(basketItem);
            } else {
                basketItem = new BasketItem();
                basketItem.setBasket(basket);
                basketItem.setProduct(product);
                basketItem.setQuantity(quantity);
                basketItemMapper.insert(basketItem);
                basket.getItems().add(basketItem); // Add to in-memory list for consistency within transaction
            }

            basket.setUpdatedAt(LocalDateTime.now());
            basketMapper.update(basket); // Update basket in DB

            stockService.persistStockUpdate(productId, -quantity); // Update product stock in DB and Redis

            refreshBasketInRedis(userId); // Refresh the basket in Redis cache

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
        BasketItem itemToRemove = basketItemMapper.findById(basketItemId)
                .orElseThrow(() -> new BusinessException(BusinessCode.BASKET_ITEM_NOT_FOUND, "Basket item not found"));

        if (!itemToRemove.getBasket().getBasketId().equals(basket.getBasketId())) {
            throw new BusinessException(BusinessCode.BASKET_ITEM_MISMATCH, "Basket item does not belong to the user's basket");
        }

        int currentQuantity = itemToRemove.getQuantity();
        Long productId = itemToRemove.getProduct().getProductId();

        if (quantityToRemove >= currentQuantity) {
            basket.getItems().remove(itemToRemove); // Remove from in-memory list
            basketItemMapper.deleteById(itemToRemove.getBasketItemId()); // Delete from DB
            stockService.persistStockUpdate(productId, currentQuantity); // Return all stock
        } else {
            itemToRemove.setQuantity(currentQuantity - quantityToRemove);
            basketItemMapper.update(itemToRemove); // Update in DB
            stockService.persistStockUpdate(productId, quantityToRemove); // Return specified quantity
        }

        basket.setUpdatedAt(LocalDateTime.now());
        basketMapper.update(basket); // Update basket in DB

        refreshBasketInRedis(userId); // Refresh the basket in Redis cache
    }

    public void saveBasket(Basket basket) {
        basketMapper.update(basket);
    }

    @Transactional
    public Basket getBasket(Long userId) {
        Basket basket = basketRedisTemplate.opsForValue().get(getBasketKey(userId));
        if (basket != null) {
            return basket;
        }

        // If not in Redis, get from DB or create new
        basket = getOrCreateBasket(userId);

        // Cache in Redis
        basketRedisTemplate.opsForValue().set(getBasketKey(userId), basket, Duration.ofMinutes(30)); // Cache for 30 minutes
        return basket;
    }

    private void refreshBasketInRedis(Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));
        Basket freshBasket = basketMapper.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(BusinessCode.BASKET_NOT_FOUND, "Basket not found for user: " + userId));
        basketRedisTemplate.opsForValue().set(getBasketKey(userId), freshBasket, Duration.ofMinutes(30));
    }
}
