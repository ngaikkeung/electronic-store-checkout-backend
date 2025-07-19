package io.github.kkngai.estorecheckout.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.dto.request.DiscountCreateRequest;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import io.github.kkngai.estorecheckout.model.Discount;
import io.github.kkngai.estorecheckout.model.DiscountType;
import io.github.kkngai.estorecheckout.service.DiscountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDiscountController.class)
class AdminDiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountService discountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDiscount_Success() throws Exception {
        DiscountCreateRequest request = new DiscountCreateRequest(
                1L, "Test Discount", DiscountType.BOGO_DEAL, "{\"buyQuantity\": 1, \"getQuantity\": 1, \"discountPercentage\": 100}", LocalDateTime.now().plusDays(7)
        );

        Discount mockDiscount = new Discount(
                1L, null, "Test Discount", DiscountType.BOGO_DEAL, "{\"buyQuantity\": 1, \"getQuantity\": 1, \"discountPercentage\": 100}", LocalDateTime.now().plusDays(7), LocalDateTime.now()
        );

        when(discountService.createDiscounts(any(List.class))).thenReturn(Collections.singletonList(mockDiscount));

        mockMvc.perform(post("/api/admin/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].discountId").value(1L))
                .andExpect(jsonPath("$.data[0].description").value("Test Discount"));
    }

    @Test
    void createDiscount_BusinessException() throws Exception {
        DiscountCreateRequest request = new DiscountCreateRequest(
                null, "Test Discount", DiscountType.BOGO_DEAL, "{\"buyQuantity\": 1, \"getQuantity\": 1, \"discountPercentage\": 100}", LocalDateTime.now().plusDays(7)
        );

        when(discountService.createDiscounts(any(List.class)))
                .thenThrow(new BusinessException(BusinessCode.PRODUCT_NOT_FOUND, "Product not found"));

        mockMvc.perform(post("/api/admin/discounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList(request))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Product not found"))
                .andExpect(jsonPath("$.success").value(false));
    }
}