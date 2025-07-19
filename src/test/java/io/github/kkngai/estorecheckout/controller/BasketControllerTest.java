package io.github.kkngai.estorecheckout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.dto.request.BasketItemRequest;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.service.BasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasketController.class)
class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketService basketService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private Basket testBasket;
    private BasketItem testBasketItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("testuser");

        testProduct = new Product();
        testProduct.setProductId(101L);
        testProduct.setName("Laptop");
        testProduct.setPrice(BigDecimal.valueOf(1200.00));
        testProduct.setStock(10);

        testBasketItem = new BasketItem();
        testBasketItem.setBasketItemId(1L);
        testBasketItem.setProduct(testProduct);
        testBasketItem.setQuantity(1);

        testBasket = new Basket();
        testBasket.setBasketId(1L);
        testBasket.setUser(testUser);
        testBasket.setItems(new ArrayList<>(Collections.singletonList(testBasketItem)));
        testBasket.setCreatedAt(LocalDateTime.now());
        testBasket.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addProductToBasket_Success() throws Exception {
        BasketItemRequest request = new BasketItemRequest();
        request.setProductId(testProduct.getProductId());
        request.setQuantity(1);

        when(basketService.addProductToBasket(anyLong(), anyLong(), anyInt())).thenReturn(testBasketItem);

        mockMvc.perform(post("/api/basket/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.basketItemId").value(testBasketItem.getBasketItemId()));
    }

    @Test
    void addProductToBasket_BusinessException() throws Exception {
        BasketItemRequest request = new BasketItemRequest();
        request.setProductId(testProduct.getProductId());
        request.setQuantity(1);

        when(basketService.addProductToBasket(anyLong(), anyLong(), anyInt()))
                .thenThrow(new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"));

        mockMvc.perform(post("/api/basket/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void removeBasketItem_Success() throws Exception {
        doNothing().when(basketService).removeBasketItem(anyLong(), anyLong(), anyInt());

        mockMvc.perform(delete("/api/basket/items/{itemId}", testBasketItem.getBasketItemId())
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Item removed from basket successfully"));
    }

    @Test
    void removeBasketItem_BusinessException() throws Exception {
        doThrow(new BusinessException(BusinessCode.BASKET_ITEM_NOT_FOUND, "Basket item not found"))
                .when(basketService).removeBasketItem(anyLong(), anyLong(), anyInt());

        mockMvc.perform(delete("/api/basket/items/{itemId}", testBasketItem.getBasketItemId())
                        .param("quantity", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.BASKET_ITEM_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Basket item not found"));
    }

    @Test
    void getBasket_Success() throws Exception {
        when(basketService.getOrCreateBasket(anyLong())).thenReturn(testBasket);

        mockMvc.perform(get("/api/basket")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.basketId").value(testBasket.getBasketId()));
    }

    @Test
    void getBasket_BusinessException() throws Exception {
        when(basketService.getOrCreateBasket(anyLong()))
                .thenThrow(new BusinessException(BusinessCode.USER_NOT_FOUND, "User not found"));

        mockMvc.perform(get("/api/basket")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
