package io.github.kkngai.estorecheckout.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.request.ProductCreateRequest;
import io.github.kkngai.estorecheckout.exception.BusinessException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private ProductCreateRequest testProductCreateRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setStock(10);
        testProduct.setCategory("Electronics");

        testProductCreateRequest = new ProductCreateRequest(
                "New Product", BigDecimal.valueOf(50.00), 5, "Books"
        );
    }

    @Test
    void createProducts_Success() throws Exception {
        when(productService.createProducts(any(List.class)))
                .thenReturn(Collections.singletonList(testProduct));

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(testProductCreateRequest))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productId").value(testProduct.getProductId()));
    }

    @Test
    void getAllProducts_Success() throws Exception {
        CustomPage<Product> customPage = new CustomPage<>(new PageImpl<>(Collections.singletonList(testProduct)));
        when(productService.getAllProducts(any(Pageable.class)))
                .thenReturn(customPage);

        mockMvc.perform(get("/api/admin/products")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].productId").value(testProduct.getProductId()));
    }

    @Test
    void updateProduct_Success() throws Exception {
        Product updatedProduct = new Product(
                1L, "Updated Product", BigDecimal.valueOf(120.00), 15, "Electronics"
        );
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/api/admin/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Updated Product"));
    }

    @Test
    void updateProduct_NotFound() throws Exception {
        when(productService.updateProduct(anyLong(), any(Product.class)))
                .thenThrow(new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"));

        mockMvc.perform(put("/api/admin/products/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/admin/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteProduct_NotFound() throws Exception {
        doThrow(new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"))
                .when(productService).deleteProduct(anyLong());

        mockMvc.perform(delete("/api/admin/products/{id}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }
}
