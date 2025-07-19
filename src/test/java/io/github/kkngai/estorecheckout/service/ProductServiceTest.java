package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.dto.response.ProductResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testProduct1 = new Product();
        testProduct1.setProductId(1L);
        testProduct1.setName("Laptop");
        testProduct1.setPrice(BigDecimal.valueOf(1200.00));
        testProduct1.setStock(50);
        testProduct1.setCategory("Electronics");

        testProduct2 = new Product();
        testProduct2.setProductId(2L);
        testProduct2.setName("Mouse");
        testProduct2.setPrice(BigDecimal.valueOf(25.00));
        testProduct2.setStock(100);
        testProduct2.setCategory("Electronics");
    }

    @Test
    void getAllProductDTOs_NoFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        CustomPage<ProductResponse> result = productService.getAllProductDTOs(null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(testProduct1.getProductId(), result.getContent().get(0).getProductId());
        assertEquals(testProduct2.getProductId(), result.getContent().get(1).getProductId());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllProductDTOs_WithCategoryFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(testProduct1);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        CustomPage<ProductResponse> result = productService.getAllProductDTOs("Electronics", null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProduct1.getProductId(), result.getContent().get(0).getProductId());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllProductDTOs_WithPriceRangeFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Collections.singletonList(testProduct1);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        CustomPage<ProductResponse> result = productService.getAllProductDTOs(null, BigDecimal.valueOf(1000), BigDecimal.valueOf(1500), null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProduct1.getProductId(), result.getContent().get(0).getProductId());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllProductDTOs_WithInStockFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        CustomPage<ProductResponse> result = productService.getAllProductDTOs(null, null, null, true, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct1, testProduct2);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(page);

        CustomPage<Product> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void getProductDtoById_Found() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct1));

        Optional<ProductResponse> result = productService.getProductDtoById(1L);

        assertTrue(result.isPresent());
        assertEquals(testProduct1.getProductId(), result.get().getProductId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductDtoById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ProductResponse> result = productService.getProductDtoById(99L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void getExistingProductById_Found() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct1));

        Product result = productService.getExistingProductById(1L);

        assertNotNull(result);
        assertEquals(testProduct1.getProductId(), result.getProductId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getExistingProductById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.getExistingProductById(99L);
        });

        assertEquals(BusinessCode.PRODUCT_NOT_FOUND, exception.getCode());
        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    void createProducts_Success() {
        ProductCreateRequest request1 = new ProductCreateRequest();
        request1.setName("Tablet");
        request1.setPrice(BigDecimal.valueOf(500.00));
        request1.setStock(30);
        request1.setCategory("Electronics");

        ProductCreateRequest request2 = new ProductCreateRequest();
        request2.setName("Keyboard");
        request2.setPrice(BigDecimal.valueOf(75.00));
        request2.setStock(80);
        request2.setCategory("Accessories");

        List<ProductCreateRequest> requests = Arrays.asList(request1, request2);

        Product savedProduct1 = new Product();
        savedProduct1.setProductId(3L);
        savedProduct1.setName("Tablet");
        savedProduct1.setPrice(BigDecimal.valueOf(500.00));
        savedProduct1.setStock(30);
        savedProduct1.setCategory("Electronics");

        Product savedProduct2 = new Product();
        savedProduct2.setProductId(4L);
        savedProduct2.setName("Keyboard");
        savedProduct2.setPrice(BigDecimal.valueOf(75.00));
        savedProduct2.setStock(80);
        savedProduct2.setCategory("Accessories");

        when(productRepository.saveAll(anyList())).thenReturn(Arrays.asList(savedProduct1, savedProduct2));

        List<Product> result = productService.createProducts(requests);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(savedProduct1.getProductId(), result.get(0).getProductId());
        assertEquals(savedProduct2.getProductId(), result.get(1).getProductId());
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateProduct_Success() {
        Product updatedDetails = new Product();
        updatedDetails.setName("Updated Laptop");
        updatedDetails.setPrice(BigDecimal.valueOf(1300.00));
        updatedDetails.setStock(60);
        updatedDetails.setCategory("Electronics");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct1));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct1);

        Product result = productService.updateProduct(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(1300.00), result.getPrice());
        assertEquals(60, result.getStock());
        assertEquals("Electronics", result.getCategory());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void updateProduct_NotFound() {
        Product updatedDetails = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            productService.updateProduct(99L, updatedDetails);
        });

        assertEquals(BusinessCode.PRODUCT_NOT_FOUND, exception.getCode());
        verify(productRepository, times(1)).findById(99L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
