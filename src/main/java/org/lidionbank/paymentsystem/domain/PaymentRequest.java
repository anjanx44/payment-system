package org.lidionbank.paymentsystem.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private String customerId;

    @NotNull
    private String country;
}