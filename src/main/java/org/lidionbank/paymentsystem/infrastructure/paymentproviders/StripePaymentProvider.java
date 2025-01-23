package org.lidionbank.paymentsystem.infrastructure.paymentproviders;


import jakarta.enterprise.context.ApplicationScoped;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

@ApplicationScoped
public class StripePaymentProvider implements PaymentProvider {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // Logic to process payment with Stripe
        // ...
        return new PaymentResponse("SUCCESS", "Payment processed successfully", "stripe_tx_12345");
    }
}