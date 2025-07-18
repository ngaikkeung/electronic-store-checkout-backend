package io.github.kkngai.estorecheckout.exception;

import io.github.kkngai.estorecheckout.model.BusinessCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(BusinessCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
