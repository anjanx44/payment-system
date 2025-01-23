// src/main/java/org/lidionbank/paymentsystem/domain/PaymentResponse.java

package org.lidionbank.paymentsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID paymentId;
    private String status;
    private String message;
    private BigDecimal amount;
    private String currency;
    private String customerId;
    private LocalDateTime timestamp;
    private String transactionId;

    // Constructor for error responses
    public PaymentResponse(String status, String message, String transactionId) {
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
    }

    // Static factory methods for common responses
    public static PaymentResponse success(UUID paymentId, BigDecimal amount, String currency,
                                          String customerId, String transactionId) {
        return PaymentResponse.builder()
                .paymentId(paymentId)
                .status("SUCCESS")
                .message("Payment processed successfully")
                .amount(amount)
                .currency(currency)
                .customerId(customerId)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static PaymentResponse pending(UUID paymentId, BigDecimal amount, String currency,
                                          String customerId, String transactionId) {
        return PaymentResponse.builder()
                .paymentId(paymentId)
                .status("PENDING")
                .message("Payment is being processed")
                .amount(amount)
                .currency(currency)
                .customerId(customerId)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static PaymentResponse failed(String message, String transactionId) {
        return PaymentResponse.builder()
                .status("FAILED")
                .message(message)
                .transactionId(transactionId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
