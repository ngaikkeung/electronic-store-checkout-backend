package io.github.kkngai.estorecheckout.exception;

import io.github.kkngai.estorecheckout.dto.response.UnifiedResponse;
import io.github.kkngai.estorecheckout.model.BusinessCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UnifiedResponse<Void> handleBusinessException(BusinessException ex) {
        return UnifiedResponse.error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifiedResponse<Void> handleRuntimeException(RuntimeException ex) {
        return UnifiedResponse.error(BusinessCode.SYSTEM_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifiedResponse<Void> handleException(Exception ex) {
        return UnifiedResponse.error(BusinessCode.SYSTEM_ERROR, "An unexpected error occurred.");
    }
}
