package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setStock(100);
    }

    @Test
    void initializeStock_Success() {
        stockService.initializeStock(testProduct.getProductId(), testProduct.getStock());

        verify(valueOperations, times(1)).set(eq("stock:product:1"), eq("100"));
        verify(redisTemplate, times(1)).expire(eq("stock:product:1"), eq(1L), eq(TimeUnit.DAYS));
    }

    @Test
    void reserveStock_StockAvailableInRedis() {
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        when(valueOperations.decrement(anyString(), anyLong())).thenReturn(90L); // 100 - 10 = 90

        boolean result = stockService.reserveStock(testProduct.getProductId(), 10);

        assertTrue(result);
        verify(redisTemplate, times(1)).hasKey(anyString());
        verify(valueOperations, times(1)).decrement(eq("stock:product:1"), eq(10L));
        verify(productRepository, never()).findById(anyLong());
        verify(valueOperations, never()).increment(anyString(), anyLong());
    }

    @Test
    void reserveStock_StockNotAvailableInRedis_InitializedFromDB() {
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(valueOperations.decrement(anyString(), anyLong())).thenReturn(90L);

        boolean result = stockService.reserveStock(testProduct.getProductId(), 10);

        assertTrue(result);
        verify(redisTemplate, times(1)).hasKey(eq("stock:product:1"));
        verify(productRepository, times(1)).findById(eq(1L));
        verify(valueOperations, times(1)).set(eq("stock:product:1"), eq("100"));
        verify(redisTemplate, times(1)).expire(eq("stock:product:1"), eq(1L), eq(TimeUnit.DAYS));
        verify(valueOperations, times(1)).decrement(eq("stock:product:1"), eq(10L));
        verify(valueOperations, never()).increment(anyString(), anyLong());
    }

    @Test
    void reserveStock_InsufficientStock() {
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        when(valueOperations.decrement(anyString(), anyLong())).thenReturn(-5L); // 100 - 105 = -5

        boolean result = stockService.reserveStock(testProduct.getProductId(), 105);

        assertFalse(result);
        verify(valueOperations, times(1)).decrement(eq("stock:product:1"), eq(105L));
        verify(valueOperations, times(1)).increment(eq("stock:product:1"), eq(105L));
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void reserveStock_ProductNotFoundDuringInitialization() {
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockService.reserveStock(testProduct.getProductId(), 10);
        });

        assertEquals(BusinessCode.PRODUCT_NOT_FOUND, exception.getCode());
        verify(redisTemplate, times(1)).hasKey(eq("stock:product:1"));
        verify(productRepository, times(1)).findById(eq(1L));
        verify(valueOperations, never()).set(anyString(), anyString());
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        verify(valueOperations, never()).decrement(anyString(), anyLong());
        verify(valueOperations, never()).increment(anyString(), anyLong());
    }

    @Test
    void persistStockUpdate_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        stockService.persistStockUpdate(testProduct.getProductId(), -10);

        assertEquals(90, testProduct.getStock()); // 100 - 10 = 90
        verify(productRepository, times(1)).findById(eq(1L));
        verify(productRepository, times(1)).save(eq(testProduct));
        verify(valueOperations, times(1)).set(eq("stock:product:1"), eq("90"));
    }

    @Test
    void persistStockUpdate_ProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            stockService.persistStockUpdate(testProduct.getProductId(), -10);
        });

        assertEquals(BusinessCode.PRODUCT_NOT_FOUND, exception.getCode());
        verify(productRepository, times(1)).findById(eq(1L));
        verify(productRepository, never()).save(any(Product.class));
        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    void rollbackStock_Success() {
        stockService.rollbackStock(testProduct.getProductId(), 5);

        verify(valueOperations, times(1)).increment(eq("stock:product:1"), eq(5L));
    }
}
