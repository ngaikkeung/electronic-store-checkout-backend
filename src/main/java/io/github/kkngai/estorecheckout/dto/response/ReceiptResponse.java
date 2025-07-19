package io.github.kkngai.estorecheckout.dto.response;

import io.github.kkngai.estorecheckout.model.Order;
import io.github.kkngai.estorecheckout.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponse {
    private Long orderId;
    private Long userId;
    private LocalDateTime orderDate;
    private List<ReceiptItem> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtPurchase;
        private BigDecimal itemTotal;
    }

    public static ReceiptResponse fromOrder(Order order, BigDecimal discountAmount) {
        ReceiptResponse receipt = new ReceiptResponse();
        receipt.setOrderId(order.getOrderId());
        receipt.setUserId(order.getUser().getUserId());
        receipt.setOrderDate(order.getCreatedAt());

        BigDecimal subtotal = BigDecimal.ZERO;
        List<ReceiptItem> receiptItems = new java.util.ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            BigDecimal itemTotal = orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            receiptItems.add(new ReceiptItem(
                    orderItem.getProduct().getProductId(),
                    orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    orderItem.getPriceAtPurchase(),
                    itemTotal
            ));
        }
        receipt.setItems(receiptItems);
        receipt.setSubtotal(subtotal);
        receipt.setDiscountAmount(discountAmount);
        receipt.setTotalAmount(subtotal.subtract(discountAmount));

        return receipt;
    }
}
