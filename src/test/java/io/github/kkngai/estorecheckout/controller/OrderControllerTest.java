package io.github.kkngai.estorecheckout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kkngai.estorecheckout.dto.response.ReceiptResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrderReceipt_Success() throws Exception {
        Long orderId = 1L;
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // Mock Product
        Product laptop = new Product(1L, "Laptop", BigDecimal.valueOf(1200.00), 50, "Electronics");
        Product mouse = new Product(2L, "Mouse", BigDecimal.valueOf(25.00), 200, "Accessories");

        // Mock OrderItems
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setProduct(laptop);
        orderItem1.setQuantity(1);
        orderItem1.setPriceAtPurchase(BigDecimal.valueOf(1200.00));
        orderItem1.setDiscountedPrice(BigDecimal.valueOf(1080.00)); // 10% off

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setProduct(mouse);
        orderItem2.setQuantity(2);
        orderItem2.setPriceAtPurchase(BigDecimal.valueOf(25.00));
        orderItem2.setDiscountedPrice(BigDecimal.valueOf(50.00)); // No discount

        // Mock Order
        User user = new User(userId, "Test User", "test@example.com", now);
        Order order = new Order(orderId, user, io.github.kkngai.estorecheckout.model.OrderStatus.DELIVERED, BigDecimal.valueOf(1130.00), now, Arrays.asList(orderItem1, orderItem2));
        orderItem1.setOrder(order);
        orderItem2.setOrder(order);

        // Mock ReceiptResponse
        ReceiptResponse.ReceiptItem receiptItem1 = new ReceiptResponse.ReceiptItem(
                laptop.getProductId(), laptop.getName(), 1, laptop.getPrice(), BigDecimal.valueOf(1200.00),
                BigDecimal.valueOf(120.00), BigDecimal.valueOf(1080.00)
        );
        ReceiptResponse.ReceiptItem receiptItem2 = new ReceiptResponse.ReceiptItem(
                mouse.getProductId(), mouse.getName(), 2, mouse.getPrice(), BigDecimal.valueOf(50.00),
                BigDecimal.valueOf(0.00), BigDecimal.valueOf(50.00)
        );

        ReceiptResponse mockReceiptResponse = new ReceiptResponse(
                orderId, userId, now, Arrays.asList(receiptItem1, receiptItem2),
                BigDecimal.valueOf(1250.00), BigDecimal.valueOf(120.00), BigDecimal.valueOf(1130.00)
        );

        when(orderService.getOrderReceipt(anyLong())).thenReturn(mockReceiptResponse);

        mockMvc.perform(get("/api/orders/{orderId}/receipt", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(orderId))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.items[0].productId").value(laptop.getProductId()))
                .andExpect(jsonPath("$.data.items[0].productName").value(laptop.getName()))
                .andExpect(jsonPath("$.data.items[0].quantity").value(1))
                .andExpect(jsonPath("$.data.items[0].originalPricePerUnit").value(1200.00))
                .andExpect(jsonPath("$.data.items[0].originalItemTotal").value(1200.00))
                .andExpect(jsonPath("$.data.items[0].discountApplied").value(120.00))
                .andExpect(jsonPath("$.data.items[0].totalPriceAfterDiscount").value(1080.00))
                .andExpect(jsonPath("$.data.items[1].productId").value(mouse.getProductId()))
                .andExpect(jsonPath("$.data.items[1].productName").value(mouse.getName()))
                .andExpect(jsonPath("$.data.items[1].quantity").value(2))
                .andExpect(jsonPath("$.data.items[1].originalPricePerUnit").value(25.00))
                .andExpect(jsonPath("$.data.items[1].originalItemTotal").value(50.00))
                .andExpect(jsonPath("$.data.items[1].discountApplied").value(0.00))
                .andExpect(jsonPath("$.data.items[1].totalPriceAfterDiscount").value(50.00))
                .andExpect(jsonPath("$.data.subtotal").value(1250.00))
                .andExpect(jsonPath("$.data.totalDiscountAmount").value(120.00))
                .andExpect(jsonPath("$.data.totalAmount").value(1130.00));
    }

    @Test
    void getOrderReceipt_NotFound() throws Exception {
        Long orderId = 99L;

        when(orderService.getOrderReceipt(anyLong()))
                .thenThrow(new BusinessException(BusinessCode.ORDER_NOT_FOUND, "Order not found"));

        mockMvc.perform(get("/api/orders/{orderId}/receipt", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BusinessCode.ORDER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value("Order not found"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getOrderReceipt_InternalServerError() throws Exception {
        Long orderId = 1L;

        when(orderService.getOrderReceipt(anyLong()))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/orders/{orderId}/receipt", orderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(BusinessCode.SYSTEM_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value("Something went wrong"))
                .andExpect(jsonPath("$.success").value(false));
    }
}
