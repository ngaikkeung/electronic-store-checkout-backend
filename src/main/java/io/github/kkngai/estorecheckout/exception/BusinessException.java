package io.github.kkngai.estorecheckout.exception;

import io.github.kkngai.estorecheckout.model.BusinessCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessCode code;

    public BusinessException(BusinessCode responseCode, String message) {
        super(message);
        this.code = responseCode;
    }
}
