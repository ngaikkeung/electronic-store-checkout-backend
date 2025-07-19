package io.github.kkngai.estorecheckout.service.discount;

import io.github.kkngai.estorecheckout.model.Basket;
import io.github.kkngai.estorecheckout.model.BasketItem;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BogoDealStrategyTest {

    @InjectMocks
    private BogoDealStrategy bogoDealStrategy;

    @Mock
    private Basket basket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Product createProduct(Long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setProductId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private BasketItem createBasketItem(Product product, int quantity) {
        BasketItem item = new BasketItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }

    private Discount createBogoDiscount(int buyQuantity, int getQuantity, BigDecimal discountPercentage) {
        Discount discount = new Discount();
        discount.setDiscountType(io.github.kkngai.estorecheckout.model.DiscountType.BOGO_DEAL);
        discount.setRules(String.format("{\"buyQuantity\":%d, \"getQuantity\":%d, \"discountPercentage\":%s}", buyQuantity, getQuantity, discountPercentage.toPlainString()));
        return discount;
    }

    @Test
    void testBuy1Get1_50PercentOff_SameProduct() {
        // Scenario: Buy 1, Get 1 50% off (same product only)
        // Products: ProductA (10.0)
        // Basket: 2x ProductA
        // Expected: 1x ProductA (10.0), 1x ProductA (5.0) -> Total 15.0 (Average per unit: 7.50)
        Product productA = createProduct(1L, "ProductA", BigDecimal.valueOf(10.0));
        BasketItem itemA = createBasketItem(productA, 2);

        when(basket.getItems()).thenReturn(Collections.singletonList(itemA));

        Discount discount = createBogoDiscount(1, 1, BigDecimal.valueOf(50));

        BigDecimal discountedPriceA = bogoDealStrategy.calculateItemDiscount(itemA, basket, discount);

        assertEquals(BigDecimal.valueOf(7.50).setScale(2, RoundingMode.HALF_UP), discountedPriceA.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testBuy2Get1_50PercentOff_SameProduct() {
        // Scenario: Buy 2, Get 1 50% off (same product only)
        // Products: ProductA (10.0)
        // Basket: 3x ProductA
        // Expected: 2x ProductA (10.0), 1x ProductA (5.0) -> Total 25.0 (Average per unit: 8.33)
        Product productA = createProduct(1L, "ProductA", BigDecimal.valueOf(10.0));
        BasketItem itemA = createBasketItem(productA, 3);

        when(basket.getItems()).thenReturn(Collections.singletonList(itemA));

        Discount discount = createBogoDiscount(2, 1, BigDecimal.valueOf(50));

        BigDecimal discountedPriceA = bogoDealStrategy.calculateItemDiscount(itemA, basket, discount);

        assertEquals(BigDecimal.valueOf(8.33).setScale(2, RoundingMode.HALF_UP), discountedPriceA.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testBuy1Get1_100PercentOff_SameProduct() {
        // Scenario: Buy 1, Get 1 100% off (free) (same product only)
        // Products: ProductX (50.0)
        // Basket: 2x ProductX
        // Expected: 1x ProductX (50.0), 1x ProductX (0.0) -> Total 50.0 (Average per unit: 25.00)
        Product productX = createProduct(10L, "ProductX", BigDecimal.valueOf(50.0));
        BasketItem itemX = createBasketItem(productX, 2);

        when(basket.getItems()).thenReturn(Collections.singletonList(itemX));

        Discount discount = createBogoDiscount(1, 1, BigDecimal.valueOf(100));

        BigDecimal discountedPriceX = bogoDealStrategy.calculateItemDiscount(itemX, basket, discount);

        assertEquals(BigDecimal.valueOf(25.00).setScale(2, RoundingMode.HALF_UP), discountedPriceX.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testNoDiscountAppliedWhenNotEnoughItems_SameProduct() {
        // Scenario: Buy 1, Get 1 50% off, but only 1 item in basket (same product only)
        Product productA = createProduct(1L, "ProductA", BigDecimal.valueOf(10.0));
        BasketItem itemA = createBasketItem(productA, 1);

        when(basket.getItems()).thenReturn(Collections.singletonList(itemA));

        Discount discount = createBogoDiscount(1, 1, BigDecimal.valueOf(50));

        BigDecimal discountedPriceA = bogoDealStrategy.calculateItemDiscount(itemA, basket, discount);

        assertEquals(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP), discountedPriceA.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testMultipleBogoSets_SameProduct() {
        // Scenario: Buy 1, Get 1 50% off (same product only)
        // Products: ProductA (10.0)
        // Basket: 4x ProductA
        // Expected: 2x ProductA (10.0), 2x ProductA (5.0) -> Total 30.0 (Average per unit: 7.50)
        Product productA = createProduct(1L, "ProductA", BigDecimal.valueOf(10.0));
        BasketItem itemA = createBasketItem(productA, 4);

        when(basket.getItems()).thenReturn(Collections.singletonList(itemA));

        Discount discount = createBogoDiscount(1, 1, BigDecimal.valueOf(50));

        BigDecimal discountedPriceA = bogoDealStrategy.calculateItemDiscount(itemA, basket, discount);

        assertEquals(BigDecimal.valueOf(7.50).setScale(2, RoundingMode.HALF_UP), discountedPriceA.setScale(2, RoundingMode.HALF_UP));
    }
}