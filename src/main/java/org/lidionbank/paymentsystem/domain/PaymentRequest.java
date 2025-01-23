package org.lidionbank.paymentsystem.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {

    private BigDecimal amount;
    private String currency;
    private String customerId;
    private String country;
    private String industry;

    // ... (Getters and Setters) ...
}