package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

@ApplicationScoped
public class PaypalPaymentProvider implements PaymentProvider {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // Logic to process payment with PayPal
        // ...
        return new PaymentResponse("SUCCESS", "Payment processed successfully", "paypal_tx_12345");
    }
}