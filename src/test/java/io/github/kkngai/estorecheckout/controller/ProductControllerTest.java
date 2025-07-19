package io.github.kkngai.estorecheckout.controller;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ProductResponse;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Product;
import io.github.kkngai.estorecheckout.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product testProduct;
    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStock(10);
        testProduct.setCategory("Electronics");

        testProductResponse = ProductResponse.convertToProductResponse(testProduct);
    }

    @Test
    void getAllProducts_Success() throws Exception {
        CustomPage<ProductResponse> customPage = new CustomPage<>(new PageImpl<>(Collections.singletonList(testProductResponse)));
        when(productService.getAllProductDTOs(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(customPage);

        mockMvc.perform(get("/api/products")
                        .param("category", "Electronics")
                        .param("priceMin", "50")
                        .param("priceMax", "150")
                        .param("inStock", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].productId").value(testProduct.getProductId()));
    }

    @Test
    void getProductById_Success() throws Exception {
        when(productService.getProductDtoById(anyLong())).thenReturn(Optional.of(testProductResponse));

        mockMvc.perform(get("/api/products/{id}", testProduct.getProductId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(testProduct.getProductId()));
    }

    @Test
    void getProductById_NotFound() throws Exception {
        when(productService.getProductDtoById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }
}
