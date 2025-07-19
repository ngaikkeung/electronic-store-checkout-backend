package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.repository.BasketItemRepository;
import io.github.kkngai.estorecheckout.repository.BasketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BasketServiceTest {

    @InjectMocks
    private BasketService basketService;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private BasketItemRepository basketItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private StockService stockService;

    @Mock
    private RedisTemplate<String, Basket> basketRedisTemplate;

    @Mock
    private ValueOperations<String, Basket> valueOperations;

    private User testUser;
    private Product testProduct;
    private Basket testBasket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(basketRedisTemplate.opsForValue()).thenReturn(valueOperations);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setProductId(101L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStock(10);

        testBasket = new Basket();
        testBasket.setBasketId(1L);
        testBasket.setUser(testUser);
        testBasket.setItems(new ArrayList<>());
        testBasket.setCreatedAt(LocalDateTime.now());
        testBasket.setUpdatedAt(LocalDateTime.now());
    }

    // getOrCreateBasket tests
    @Test
    void getOrCreateBasket_UserExistsAndHasBasket() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));

        Basket result = basketService.getOrCreateBasket(testUser.getUserId());

        assertEquals(testBasket.getBasketId(), result.getBasketId());
        verify(basketRepository, times(1)).findByUser(testUser);
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    void getOrCreateBasket_UserExistsButNoBasket() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.empty());
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);

        Basket result = basketService.getOrCreateBasket(testUser.getUserId());

        assertEquals(testBasket.getBasketId(), result.getBasketId());
        verify(basketRepository, times(1)).findByUser(testUser);
        verify(basketRepository, times(1)).save(any(Basket.class));
    }

    @Test
    void getOrCreateBasket_UserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> basketService.getOrCreateBasket(testUser.getUserId()));

        assertEquals(BusinessCode.USER_NOT_FOUND, exception.getCode());
        verify(basketRepository, never()).findByUser(any(User.class));
        verify(basketRepository, never()).save(any(Basket.class));
    }

    // addProductToBasket tests
    @Test
    void addProductToBasket_NewItem_Success() {
        when(stockService.reserveStock(anyLong(), anyInt())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);

        BasketItem result = basketService.addProductToBasket(testUser.getUserId(), testProduct.getProductId(), 1);

        assertNotNull(result);
        assertEquals(1, result.getQuantity());
        assertEquals(testProduct.getProductId(), result.getProduct().getProductId());
        verify(stockService, times(1)).reserveStock(testProduct.getProductId(), 1);
        verify(stockService, times(1)).persistStockUpdate(testProduct.getProductId(), -1);
        verify(basketRepository, times(1)).save(testBasket);
        verify(valueOperations, times(1)).set(anyString(), eq(testBasket), any(Duration.class));
    }

    @Test
    void addProductToBasket_ExistingItem_Success() {
        BasketItem existingBasketItem = new BasketItem();
        existingBasketItem.setProduct(testProduct);
        existingBasketItem.setQuantity(2);
        testBasket.getItems().add(existingBasketItem);

        when(stockService.reserveStock(anyLong(), anyInt())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);

        BasketItem result = basketService.addProductToBasket(testUser.getUserId(), testProduct.getProductId(), 1);

        assertNotNull(result);
        assertEquals(3, result.getQuantity()); // 2 (existing) + 1 (new) = 3
        assertEquals(testProduct.getProductId(), result.getProduct().getProductId());
        verify(stockService, times(1)).reserveStock(testProduct.getProductId(), 1);
        verify(stockService, times(1)).persistStockUpdate(testProduct.getProductId(), -1);
        verify(basketRepository, times(1)).save(testBasket);
        verify(valueOperations, times(1)).set(anyString(), eq(testBasket), any(Duration.class));
    }

    @Test
    void addProductToBasket_OutOfStock() {
        when(stockService.reserveStock(anyLong(), anyInt())).thenReturn(false);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            basketService.addProductToBasket(testUser.getUserId(), testProduct.getProductId(), 1);
        });

        assertEquals(BusinessCode.PRODUCT_OUT_OF_STOCK, exception.getCode());
        verify(stockService, never()).persistStockUpdate(anyLong(), anyInt());
        verify(basketRepository, never()).save(any(Basket.class));
        verify(stockService, never()).rollbackStock(anyLong(), anyInt());
    }

    @Test
    void addProductToBasket_ProductServiceThrowsException_RollbackStock() {
        when(stockService.reserveStock(anyLong(), anyInt())).thenReturn(true);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(productService.getExistingProductById(anyLong())).thenThrow(new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            basketService.addProductToBasket(testUser.getUserId(), testProduct.getProductId(), 1);
        });

        assertEquals(BusinessCode.SYSTEM_ERROR, exception.getCode());
        verify(stockService, times(1)).reserveStock(testProduct.getProductId(), 1);
        verify(stockService, times(1)).rollbackStock(testProduct.getProductId(), 1);
        verify(basketRepository, never()).save(any(Basket.class));
    }

    // removeBasketItem tests
    @Test
    void removeBasketItem_PartialQuantity_Success() {
        BasketItem existingBasketItem = new BasketItem();
        existingBasketItem.setBasketItemId(10L);
        existingBasketItem.setProduct(testProduct);
        existingBasketItem.setQuantity(5);
        existingBasketItem.setBasket(testBasket);
        testBasket.getItems().add(existingBasketItem);

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findById(anyLong())).thenReturn(Optional.of(existingBasketItem));
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);

        basketService.removeBasketItem(testUser.getUserId(), existingBasketItem.getBasketItemId(), 2);

        assertEquals(3, existingBasketItem.getQuantity());
        verify(basketItemRepository, times(1)).save(existingBasketItem);
        verify(stockService, times(1)).persistStockUpdate(testProduct.getProductId(), 2);
        verify(basketRepository, times(1)).save(testBasket);
        verify(valueOperations, times(1)).set(anyString(), eq(testBasket), any(Duration.class));
    }

    @Test
    void removeBasketItem_FullQuantity_Success() {
        BasketItem existingBasketItem = new BasketItem();
        existingBasketItem.setBasketItemId(10L);
        existingBasketItem.setProduct(testProduct);
        existingBasketItem.setQuantity(2);
        existingBasketItem.setBasket(testBasket);
        testBasket.getItems().add(existingBasketItem);

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findById(anyLong())).thenReturn(Optional.of(existingBasketItem));
        when(productService.getExistingProductById(anyLong())).thenReturn(testProduct);
        when(basketRepository.save(any(Basket.class))).thenReturn(testBasket);

        basketService.removeBasketItem(testUser.getUserId(), existingBasketItem.getBasketItemId(), 2);

        assertTrue(testBasket.getItems().isEmpty());
        verify(basketItemRepository, times(1)).delete(existingBasketItem);
        verify(stockService, times(1)).persistStockUpdate(testProduct.getProductId(), 2);
        verify(basketRepository, times(1)).save(testBasket);
        verify(valueOperations, times(1)).set(anyString(), eq(testBasket), any(Duration.class));
    }

    @Test
    void removeBasketItem_ItemNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            basketService.removeBasketItem(testUser.getUserId(), 99L, 1);
        });

        assertEquals(BusinessCode.BASKET_ITEM_NOT_FOUND, exception.getCode());
        verify(stockService, never()).persistStockUpdate(anyLong(), anyInt());
        verify(basketRepository, never()).save(any(Basket.class));
    }

    @Test
    void removeBasketItem_ItemDoesNotBelongToUserBasket() {
        Basket otherBasket = new Basket();
        otherBasket.setBasketId(2L);
        User otherUser = new User();
        otherUser.setUserId(2L);
        otherBasket.setUser(otherUser);

        BasketItem existingBasketItem = new BasketItem();
        existingBasketItem.setBasketItemId(10L);
        existingBasketItem.setProduct(testProduct);
        existingBasketItem.setQuantity(5);
        existingBasketItem.setBasket(otherBasket); // Item belongs to another basket

        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketRepository.findByUser(any(User.class))).thenReturn(Optional.of(testBasket));
        when(basketItemRepository.findById(anyLong())).thenReturn(Optional.of(existingBasketItem));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            basketService.removeBasketItem(testUser.getUserId(), existingBasketItem.getBasketItemId(), 1);
        });

        assertEquals(BusinessCode.BASKET_ITEM_MISMATCH, exception.getCode());
        verify(stockService, never()).persistStockUpdate(anyLong(), anyInt());
        verify(basketRepository, never()).save(any(Basket.class));
    }
}
