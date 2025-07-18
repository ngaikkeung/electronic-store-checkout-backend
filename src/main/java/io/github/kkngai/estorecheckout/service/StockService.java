package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private String getStockKey(Long productId) {
        return "stock:product:" + productId;
    }

    public void initializeStock(Long productId, Integer stock) {
        redisTemplate.opsForValue().set(getStockKey(productId), String.valueOf(stock));
        redisTemplate.expire(getStockKey(productId), 1, TimeUnit.DAYS);
    }

    public boolean reserveStock(Long productId, Integer quantity) {
        String stockKey = getStockKey(productId);

        if (Boolean.FALSE.equals(redisTemplate.hasKey(stockKey))) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + productId));
            initializeStock(productId, product.getStock());
        }

        Long newStock = redisTemplate.opsForValue().decrement(stockKey, quantity);

        if (newStock >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            return false;
        }
    }

    @Transactional
    public void persistStockUpdate(Long productId, Integer quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found with id: " + productId));
        product.setStock(product.getStock() + quantityChange);
        productRepository.save(product);

        redisTemplate.opsForValue().set(getStockKey(productId), String.valueOf(product.getStock()));
    }

    public void rollbackStock(Long productId, Integer quantity) {
        redisTemplate.opsForValue().increment(getStockKey(productId), quantity);
    }
}
