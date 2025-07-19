package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.DiscountType;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.repository.DiscountRepository;
import io.github.kkngai.estorecheckout.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DiscountServiceTest {

    @InjectMocks
    private DiscountService discountService;

    @Mock
    private DiscountRepository discountRepository;

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;
    private Discount testDiscount1;
    private Discount testDiscount2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testProduct = new Product();
        testProduct.setProductId(101L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStock(10);
        testProduct.setCategory("Electronics");

        testDiscount1 = new Discount();
        testDiscount1.setDiscountId(1L);
        testDiscount1.setDescription("Summer Sale");
        testDiscount1.setDiscountType(DiscountType.PERCENTAGE);
        testDiscount1.setRules("{\"percentage\":10}");
        testDiscount1.setExpirationDate(LocalDateTime.now().plusDays(10));

        testDiscount2 = new Discount();
        testDiscount2.setDiscountId(2L);
        testDiscount2.setDescription("Winter Sale");
        testDiscount2.setDiscountType(DiscountType.FIXED_AMOUNT_OFF);
        testDiscount2.setRules("{\"amount\":5}");
        testDiscount2.setExpirationDate(LocalDateTime.now().minusDays(5)); // Expired discount
    }

    @Test
    void saveDiscount_Success() {
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount1);

        Discount result = discountService.saveDiscount(testDiscount1);

        assertNotNull(result);
        assertEquals(testDiscount1.getDiscountId(), result.getDiscountId());
        verify(discountRepository, times(1)).save(testDiscount1);
    }

    @Test
    void createDiscounts_Success_WithProduct() {
        DiscountCreateRequest request = new DiscountCreateRequest();
        request.setDescription("New Discount");
        request.setDiscountType(DiscountType.BOGO_DEAL);
        request.setRules("{\"buyQuantity\":1, \"getQuantity\":1, \"discountPercentage\":50}");
        request.setProductId(testProduct.getProductId());

        when(productRepository.findById(testProduct.getProductId())).thenReturn(Optional.of(testProduct));
        when(discountRepository.saveAll(anyList())).thenReturn(Collections.singletonList(testDiscount1));

        List<Discount> result = discountService.createDiscounts(Collections.singletonList(request));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findById(testProduct.getProductId());
        verify(discountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createDiscounts_Success_WithoutProduct() {
        DiscountCreateRequest request = new DiscountCreateRequest();
        request.setDescription("New Discount");
        request.setDiscountType(DiscountType.BOGO_DEAL);
        request.setRules("{\"buyQuantity\":1, \"getQuantity\":1, \"discountPercentage\":50}");

        when(discountRepository.saveAll(anyList())).thenReturn(Collections.singletonList(testDiscount1));

        List<Discount> result = discountService.createDiscounts(Collections.singletonList(request));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, never()).findById(anyLong());
        verify(discountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createDiscounts_ProductNotFound() {
        DiscountCreateRequest request = new DiscountCreateRequest();
        request.setProductId(999L);

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            discountService.createDiscounts(Collections.singletonList(request));
        });

        assertEquals(BusinessCode.PRODUCT_NOT_FOUND, exception.getCode());
        verify(productRepository, times(1)).findById(999L);
        verify(discountRepository, never()).saveAll(anyList());
    }

    @Test
    void updateDiscount_Success() {
        Discount updatedDetails = new Discount();
        updatedDetails.setDescription("Updated Sale");
        updatedDetails.setDiscountType(DiscountType.FIXED_AMOUNT_OFF);
        updatedDetails.setRules("{\"amount\":10}");
        updatedDetails.setExpirationDate(LocalDateTime.now().plusDays(20));
        updatedDetails.setProduct(testProduct);

        when(discountRepository.findById(anyLong())).thenReturn(Optional.of(testDiscount1));
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount1);

        Discount result = discountService.updateDiscount(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Sale", result.getDescription());
        assertEquals(DiscountType.FIXED_AMOUNT_OFF, result.getDiscountType());
        verify(discountRepository, times(1)).findById(1L);
        verify(discountRepository, times(1)).save(testDiscount1);
    }

    @Test
    void updateDiscount_NotFound() {
        Discount updatedDetails = new Discount();
        when(discountRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            discountService.updateDiscount(99L, updatedDetails);
        });

        assertEquals(BusinessCode.DISCOUNT_NOT_FOUND, exception.getCode());
        verify(discountRepository, times(1)).findById(99L);
        verify(discountRepository, never()).save(any(Discount.class));
    }

    @Test
    void getAllDiscounts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Discount> discounts = Arrays.asList(testDiscount1, testDiscount2);
        Page<Discount> page = new PageImpl<>(discounts, pageable, discounts.size());

        when(discountRepository.findAll(pageable)).thenReturn(page);

        CustomPage<Discount> result = discountService.getAllDiscounts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(testDiscount1.getDiscountId(), result.getContent().get(0).getDiscountId());
        verify(discountRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllActiveDiscountsByProductId_Success() {
        when(discountRepository.findByProduct_ProductIdAndExpirationDateAfterOrExpirationDateIsNull(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(testDiscount1));

        List<Discount> result = discountService.getAllActiveDiscountsByProductId(LocalDateTime.now(), testProduct.getProductId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDiscount1.getDiscountId(), result.get(0).getDiscountId());
        verify(discountRepository, times(1)).findByProduct_ProductIdAndExpirationDateAfterOrExpirationDateIsNull(eq(testProduct.getProductId()), any(LocalDateTime.class));
    }

    @Test
    void getAllActiveDiscountsByProductId_NoActiveDiscounts() {
        when(discountRepository.findByProduct_ProductIdAndExpirationDateAfterOrExpirationDateIsNull(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        List<Discount> result = discountService.getAllActiveDiscountsByProductId(LocalDateTime.now(), testProduct.getProductId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(discountRepository, times(1)).findByProduct_ProductIdAndExpirationDateAfterOrExpirationDateIsNull(eq(testProduct.getProductId()), any(LocalDateTime.class));
    }
}
