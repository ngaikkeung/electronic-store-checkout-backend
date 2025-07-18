package io.github.kkngai.estorecheckout.exception;

import io.github.kkngai.estorecheckout.model.response.ResponseCode;
import io.github.kkngai.estorecheckout.model.response.UnifiedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifiedResponse<Void> handleRuntimeException(RuntimeException ex) {
        return UnifiedResponse.error(ResponseCode.SYSTEM_ERROR.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifiedResponse<Void> handleException(Exception ex) {
        return UnifiedResponse.error(ResponseCode.SYSTEM_ERROR.getCode(), "An unexpected error occurred.");
    }
}
