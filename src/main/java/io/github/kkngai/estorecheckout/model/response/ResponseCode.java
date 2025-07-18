package io.github.kkngai.estorecheckout.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS("0"),
    SYSTEM_ERROR("9999");

    private final String code;
}
