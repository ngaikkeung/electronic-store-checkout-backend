package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.service.discount.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    @InjectMocks
    private PricingService pricingService;

    @Mock
    private DiscountService discountService;

    @Mock
    private PercentageDiscountStrategy percentageDiscountStrategy;
    @Mock
    private FixedAmountOffStrategy fixedAmountOffStrategy;
    @Mock
    private SpendThresholdStrategy spendThresholdStrategy;
    @Mock
    private BundlePriceStrategy bundlePriceStrategy;
    @Mock
    private BogoDealStrategy bogoDealStrategy;

    private Product product1;
    private Product product2;
    private BasketItem basketItem1;
    private BasketItem basketItem2;
    private Basket basket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        pricingService = new PricingService(
                discountService,
                percentageDiscountStrategy,
                fixedAmountOffStrategy,
                spendThresholdStrategy,
                bundlePriceStrategy,
                bogoDealStrategy
        );

        product1 = new Product();
        product1.setProductId(1L);
        product1.setName("Laptop");
        product1.setPrice(BigDecimal.valueOf(1000.00));
        product1.setStock(10);

        product2 = new Product();
        product2.setProductId(2L);
        product2.setName("Mouse");
        product2.setPrice(BigDecimal.valueOf(25.00));
        product2.setStock(50);

        basketItem1 = new BasketItem();
        basketItem1.setProduct(product1);
        basketItem1.setQuantity(1);

        basketItem2 = new BasketItem();
        basketItem2.setProduct(product2);
        basketItem2.setQuantity(2);

        basket = new Basket();
        basket.setItems(Arrays.asList(basketItem1, basketItem2));
    }

    @Test
    void calculateItemPriceWithDiscount_NoActiveDiscounts() {
        when(discountService.getAllActiveDiscountsByProductId(any(LocalDateTime.class), anyLong()))
                .thenReturn(Collections.emptyList());

        BigDecimal result = pricingService.calculateItemPriceWithDiscount(basketItem1, basket);

        // Expected: Original price for basketItem1 (1000.00 * 1) = 1000.00
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2, BigDecimal.ROUND_HALF_UP), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void calculateItemPriceWithDiscount_WithPercentageDiscount() {
        Discount percentageDiscount = new Discount();
        percentageDiscount.setDiscountId(10L);
        percentageDiscount.setDiscountType(DiscountType.PERCENTAGE);
        percentageDiscount.setRules("{\"percentage\":10}");

        when(discountService.getAllActiveDiscountsByProductId(any(LocalDateTime.class), eq(product1.getProductId())))
                .thenReturn(Collections.singletonList(percentageDiscount));

        // Mock the strategy to return the expected discounted price for basketItem1
        when(percentageDiscountStrategy.calculateItemDiscount(eq(basketItem1), eq(basket), eq(percentageDiscount)))
                .thenReturn(BigDecimal.valueOf(900.00)); // 1000 * 0.9

        BigDecimal result = pricingService.calculateItemPriceWithDiscount(basketItem1, basket);

        assertEquals(BigDecimal.valueOf(900.00).setScale(2, BigDecimal.ROUND_HALF_UP), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void calculateItemPriceWithDiscount_WithFixedAmountOffDiscount() {
        Discount fixedAmountOffDiscount = new Discount();
        fixedAmountOffDiscount.setDiscountId(20L);
        fixedAmountOffDiscount.setDiscountType(DiscountType.FIXED_AMOUNT_OFF);
        fixedAmountOffDiscount.setRules("{\"amount\":5}");

        when(discountService.getAllActiveDiscountsByProductId(any(LocalDateTime.class), eq(product2.getProductId())))
                .thenReturn(Collections.singletonList(fixedAmountOffDiscount));

        // Mock the strategy to return the expected discounted price for basketItem2
        when(fixedAmountOffStrategy.calculateItemDiscount(eq(basketItem2), eq(basket), eq(fixedAmountOffDiscount)))
                .thenReturn(BigDecimal.valueOf(40.00)); // (25 - 5) * 2 = 40.00

        BigDecimal result = pricingService.calculateItemPriceWithDiscount(basketItem2, basket);

        assertEquals(BigDecimal.valueOf(40.00).setScale(2, BigDecimal.ROUND_HALF_UP), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void calculateItemPriceWithDiscount_WithBogoDiscount() {
        Discount bogoDiscount = new Discount();
        bogoDiscount.setDiscountId(30L);
        bogoDiscount.setDiscountType(DiscountType.BOGO_DEAL);
        bogoDiscount.setRules("{\"buyQuantity\":1, \"getQuantity\":1, \"discountPercentage\":50}");

        when(discountService.getAllActiveDiscountsByProductId(any(LocalDateTime.class), eq(product1.getProductId())))
                .thenReturn(Collections.singletonList(bogoDiscount));

        // Mock the strategy to return the expected discounted price for basketItem1
        when(bogoDealStrategy.calculateItemDiscount(eq(basketItem1), eq(basket), eq(bogoDiscount)))
                .thenReturn(BigDecimal.valueOf(500.00)); // Assuming BOGO makes it 50% off for 1 item

        BigDecimal result = pricingService.calculateItemPriceWithDiscount(basketItem1, basket);

        assertEquals(BigDecimal.valueOf(500.00).setScale(2, BigDecimal.ROUND_HALF_UP), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Test
    void calculateItemPriceWithDiscount_MultipleDiscounts_BestApplied() {
        Discount percentageDiscount = new Discount();
        percentageDiscount.setDiscountId(10L);
        percentageDiscount.setDiscountType(DiscountType.PERCENTAGE);
        percentageDiscount.setRules("{\"percentage\":10}"); // 10% off -> 900.00

        Discount fixedAmountOffDiscount = new Discount();
        fixedAmountOffDiscount.setDiscountId(20L);
        fixedAmountOffDiscount.setDiscountType(DiscountType.FIXED_AMOUNT_OFF);
        fixedAmountOffDiscount.setRules("{\"amount\":150}"); // 150 off -> 850.00

        when(discountService.getAllActiveDiscountsByProductId(any(LocalDateTime.class), eq(product1.getProductId())))
                .thenReturn(Arrays.asList(percentageDiscount, fixedAmountOffDiscount));

        // Mock strategies to return their respective discounted prices
        when(percentageDiscountStrategy.calculateItemDiscount(eq(basketItem1), eq(basket), eq(percentageDiscount)))
                .thenReturn(BigDecimal.valueOf(900.00));
        when(fixedAmountOffStrategy.calculateItemDiscount(eq(basketItem1), eq(basket), eq(fixedAmountOffDiscount)))
                .thenReturn(BigDecimal.valueOf(850.00));

        BigDecimal result = pricingService.calculateItemPriceWithDiscount(basketItem1, basket);

        // Fixed amount off (850.00) is better than percentage (900.00)
        assertEquals(BigDecimal.valueOf(850.00).setScale(2, BigDecimal.ROUND_HALF_UP), result.setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}