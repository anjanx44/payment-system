package org.lidionbank.paymentsystem.infrastructure.paymentproviders;


import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentProviderFactory {
    public PaymentProvider getPaymentProvider(String providerName) {
        if ("Stripe".equalsIgnoreCase(providerName)) {
            return new StripePaymentProvider();
        } else if ("PayPal".equalsIgnoreCase(providerName)) {
            return new PaypalPaymentProvider();
        } else {
            // Handle unknown provider
            throw new IllegalArgumentException("Unknown payment provider: " + providerName);
        }
    }
}