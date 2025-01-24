package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

@ApplicationScoped
public class PaymentProviderFactory {

    @Inject
    StripePaymentProvider stripeProvider;

    @Inject
    PaddlePaymentProvider paddleProvider;

    public PaymentProvider getPaymentProvider(String providerName) {
        return switch (providerName.toUpperCase()) {
            case "STRIPE" -> stripeProvider;
            case "PADDLE" -> paddleProvider;
            default -> throw new IllegalArgumentException("Unknown payment provider: " + providerName +
                    ". Supported providers are: STRIPE, PADDLE");
        };
    }

    public PaymentProvider getPaymentProvider(PaymentProviderType providerType) {
        return switch (providerType) {
            case STRIPE -> stripeProvider;
            case PADDLE -> paddleProvider;
        };
    }
}
