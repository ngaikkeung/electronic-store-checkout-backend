package io.github.kkngai.estorecheckout.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedResponse<T> {
    private String code;
    private String message;
    private T data;
    private boolean success;

    public static <T> UnifiedResponse<T> success() {
        return new UnifiedResponse<>(ResponseCode.SUCCESS.getCode(), null, null, true);
    }

    public static <T> UnifiedResponse<T> success(T data) {
        return new UnifiedResponse<>(ResponseCode.SUCCESS.getCode(), null, data, true);
    }

    public static <T> UnifiedResponse<T> error(String message) {
        return new UnifiedResponse<>(ResponseCode.SYSTEM_ERROR.getCode(), message, null, false);
    }

    public static <T> UnifiedResponse<T> error(String code, String message) {
        return new UnifiedResponse<>(code, message, null, false);
    }
}
