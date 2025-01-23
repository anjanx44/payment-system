package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

@FunctionalInterface
public interface PaymentProvider {
    PaymentResponse processPayment(PaymentRequest request);
}