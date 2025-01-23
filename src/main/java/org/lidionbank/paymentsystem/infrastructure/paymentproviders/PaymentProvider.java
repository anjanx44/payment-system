package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

public interface PaymentProvider {
    PaymentResponse processPayment(PaymentRequest request);
    boolean supports(PaymentRequest request);
    String getProviderName();
}
