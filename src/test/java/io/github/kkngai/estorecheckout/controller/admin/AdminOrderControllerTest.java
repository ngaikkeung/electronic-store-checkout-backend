package io.github.kkngai.estorecheckout.controller.admin;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.model.OrderStatus;
import io.github.kkngai.estorecheckout.model.User;
import io.github.kkngai.estorecheckout.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminOrderController.class)
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setUserId(1L);
        testUser.setName("testuser");

        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PROCESSING);
        testOrder.setTotalPrice(BigDecimal.valueOf(100.00));
        testOrder.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllOrders_Success() throws Exception {
        CustomPage<Order> customPage = new CustomPage<>(new PageImpl<>(Collections.singletonList(testOrder)));
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(customPage);

        mockMvc.perform(get("/api/admin/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].orderId").value(testOrder.getOrderId()));
    }
}
