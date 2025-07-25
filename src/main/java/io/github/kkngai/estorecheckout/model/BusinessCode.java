package io.github.kkngai.estorecheckout.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessCode {
    SUCCESS("0"),
    USER_NOT_FOUND("1001"),
    PRODUCT_NOT_FOUND("1002"),
    BASKET_ITEM_NOT_FOUND("1003"),
    BASKET_ITEM_MISMATCH("1004"),
    EMPTY_BASKET("1005"),
    ORDER_NOT_FOUND("1006"),
    DISCOUNT_NOT_FOUND("1007"),
    INVALID_DISCOUNT_RULES("1008"),
    PRODUCT_OUT_OF_STOCK("1009"),
    SYSTEM_ERROR("9999");

    private final String code;
}