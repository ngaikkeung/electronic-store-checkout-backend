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
    private BigDecimal totalDiscountAmount;
    private BigDecimal totalAmount;

    public static ReceiptResponse fromOrder(Order order) {
        ReceiptResponse receipt = new ReceiptResponse();
        receipt.setOrderId(order.getOrderId());
        receipt.setUserId(order.getUser().getUserId());
        receipt.setOrderDate(order.getCreatedAt());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscountAmount = BigDecimal.ZERO;
        List<ReceiptItem> receiptItems = new java.util.ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            BigDecimal originalPricePerUnit = orderItem.getPriceAtPurchase();
            BigDecimal originalItemTotal = originalPricePerUnit.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            BigDecimal discountedItemTotal = orderItem.getDiscountedPrice();
            BigDecimal discountApplied = originalItemTotal.subtract(discountedItemTotal);

            subtotal = subtotal.add(originalItemTotal);
            totalDiscountAmount = totalDiscountAmount.add(discountApplied);

            receiptItems.add(new ReceiptItem(
                    orderItem.getProduct().getProductId(),
                    orderItem.getProduct().getName(),
                    orderItem.getQuantity(),
                    originalPricePerUnit,
                    originalItemTotal,
                    discountApplied,
                    discountedItemTotal
            ));
        }
        receipt.setItems(receiptItems);
        receipt.setSubtotal(subtotal);
        receipt.setTotalDiscountAmount(totalDiscountAmount);
        receipt.setTotalAmount(order.getTotalPrice());

        return receipt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal originalPricePerUnit;
        private BigDecimal originalItemTotal;
        private BigDecimal discountApplied;
        private BigDecimal totalPriceAfterDiscount;
    }
}
