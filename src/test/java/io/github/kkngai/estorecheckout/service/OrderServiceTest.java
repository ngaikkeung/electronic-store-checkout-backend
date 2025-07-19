package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.dto.CustomPage;
import io.github.kkngai.estorecheckout.dto.response.ReceiptResponse;
import io.github.kkngai.estorecheckout.exception.BusinessException;
import io.github.kkngai.estorecheckout.model.*;
import io.github.kkngai.estorecheckout.repository.OrderItemRepository;
import io.github.kkngai.estorecheckout.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private BasketService basketService;
    @Mock
    private UserService userService;
    @Mock
    private PricingService pricingService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Basket testBasket;
    private BasketItem testBasketItem;
    private Order testOrder;
    private OrderItem testOrderItem;

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

        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PROCESSING);
        testOrderItem = new OrderItem();
        testOrderItem.setOrderItemId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(1);
        testOrderItem.setPriceAtPurchase(BigDecimal.valueOf(1200.00));
        testOrderItem.setDiscountedPrice(BigDecimal.valueOf(1200.00));

        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setTotalPrice(BigDecimal.valueOf(1200.00));
        testOrder.setOrderItems(new ArrayList<>(Collections.singletonList(testOrderItem)));
    }

    @Test
    void getAllOrders_shouldReturnCustomPageOfOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(Collections.singletonList(testOrder), pageable, 1);
        when(orderRepository.findAll(pageable)).thenReturn(page);

        CustomPage<Order> result = orderService.getAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testOrder.getOrderId(), result.getContent().get(0).getOrderId());
        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void createOrderFromBasket_shouldCreateOrderSuccessfully() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketService.getOrCreateBasket(anyLong())).thenReturn(testBasket);
        when(pricingService.calculateItemPriceWithDiscount(any(BasketItem.class), any(Basket.class)))
                .thenReturn(BigDecimal.valueOf(1200.00));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        Order createdOrder = orderService.createOrderFromBasket(testUser.getUserId());

        assertNotNull(createdOrder);
        assertEquals(testOrder.getOrderId(), createdOrder.getOrderId());
        assertEquals(OrderStatus.PROCESSING, createdOrder.getStatus());
        assertEquals(0, BigDecimal.valueOf(1200.00).compareTo(createdOrder.getTotalPrice()));

        verify(userService, times(1)).getUserById(testUser.getUserId());
        verify(basketService, times(1)).getOrCreateBasket(testUser.getUserId());
        verify(orderRepository, times(2)).save(any(Order.class)); // Initial save and final update
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(basketService, times(1)).saveBasketAndCache(testBasket);
        assertTrue(testBasket.getItems().isEmpty());
    }

    @Test
    void createOrderFromBasket_shouldThrowException_whenUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrderFromBasket(testUser.getUserId()));

        assertEquals(BusinessCode.USER_NOT_FOUND, exception.getCode());
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserById(testUser.getUserId());
        verifyNoInteractions(basketService, orderRepository, orderItemRepository, pricingService);
    }

    @Test
    void createOrderFromBasket_shouldThrowException_whenBasketIsEmpty() {
        testBasket.setItems(new ArrayList<>());
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(basketService.getOrCreateBasket(anyLong())).thenReturn(testBasket);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.createOrderFromBasket(testUser.getUserId()));

        assertEquals(BusinessCode.EMPTY_BASKET, exception.getCode());
        assertEquals("Cannot create order from an empty basket", exception.getMessage());
        verify(userService, times(1)).getUserById(testUser.getUserId());
        verify(basketService, times(1)).getOrCreateBasket(testUser.getUserId());
        verifyNoInteractions(orderRepository, orderItemRepository, pricingService);
    }

    @Test
    void getUserOrders_shouldReturnCustomPageOfOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(Collections.singletonList(testOrder), pageable, 1);
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser(testUser, pageable)).thenReturn(page);

        CustomPage<Order> result = orderService.getUserOrders(testUser.getUserId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testOrder.getOrderId(), result.getContent().get(0).getOrderId());
        verify(userService, times(1)).getUserById(testUser.getUserId());
        verify(orderRepository, times(1)).findByUser(testUser, pageable);
    }

    @Test
    void getUserOrders_shouldThrowException_whenUserNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.getUserOrders(testUser.getUserId(), PageRequest.of(0, 10)));

        assertEquals(BusinessCode.USER_NOT_FOUND, exception.getCode());
        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).getUserById(testUser.getUserId());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        Optional<Order> result = orderService.getOrderById(testOrder.getOrderId());

        assertTrue(result.isPresent());
        assertEquals(testOrder.getOrderId(), result.get().getOrderId());
        verify(orderRepository, times(1)).findById(testOrder.getOrderId());
    }

    @Test
    void getOrderById_shouldReturnEmptyOptional_whenNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(testOrder.getOrderId());

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(testOrder.getOrderId());
    }

    @Test
    void getOrderReceipt_shouldReturnReceiptResponse() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        ReceiptResponse result = orderService.getOrderReceipt(testOrder.getOrderId());

        assertNotNull(result);
        assertEquals(testOrder.getOrderId(), result.getOrderId());
        verify(orderRepository, times(1)).findById(testOrder.getOrderId());
    }

    @Test
    void getOrderReceipt_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.getOrderReceipt(testOrder.getOrderId()));

        assertEquals(BusinessCode.ORDER_NOT_FOUND, exception.getCode());
        assertEquals("Order not found", exception.getMessage());
        verify(orderRepository, times(1)).findById(testOrder.getOrderId());
    }
}
