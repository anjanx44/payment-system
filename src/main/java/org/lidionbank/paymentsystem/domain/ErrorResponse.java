package org.lidionbank.paymentsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String code;
    private LocalDateTime timestamp;
    private String path;
    private String details;

    // Static factory methods for common error scenarios
    public static ErrorResponse notFound(String message, String path) {
        return ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ErrorResponse badRequest(String message, String path) {
        return ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ErrorResponse internalError(String message, String path) {
        return ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    public static ErrorResponse validationError(String message, String details, String path) {
        return ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .details(details)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
