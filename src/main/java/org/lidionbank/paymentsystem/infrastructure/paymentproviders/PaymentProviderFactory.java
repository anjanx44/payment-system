package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * The PaymentProviderFactory class is responsible for providing instances of specific payment providers
 * based on the provider's name or type. This class acts as a simple factory to decouple the creation of
 * payment provider instances from the business logic.
 */
@ApplicationScoped
public class PaymentProviderFactory {

    // Injecting the Stripe payment provider instance
    @Inject
    StripePaymentProvider stripeProvider;

    // Injecting the Paddle payment provider instance
    @Inject
    PaddlePaymentProvider paddleProvider;

    /**
     * Retrieves a PaymentProvider based on the provider's name (e.g., "STRIPE" or "PADDLE").
     *
     * @param providerName the name of the payment provider
     * @return the corresponding PaymentProvider instance
     * @throws IllegalArgumentException if the provider name is unknown or unsupported
     */
    public PaymentProvider getPaymentProvider(String providerName) {
        // Select provider based on the name
        return switch (providerName.toUpperCase()) {
            case "STRIPE" -> stripeProvider; // Return the Stripe provider
            case "PADDLE" -> paddleProvider; // Return the Paddle provider
            default -> throw new IllegalArgumentException("Unknown payment provider: " + providerName +
                    ". Supported providers are: STRIPE, PADDLE"); // Throw exception if the provider is not supported
        };
    }

    /**
     * Retrieves a PaymentProvider based on the provider type (PaymentProviderType enum).
     *
     * @param providerType the type of the payment provider (STRIPE, PADDLE)
     * @return the corresponding PaymentProvider instance
     */
    public PaymentProvider getPaymentProvider(PaymentProviderType providerType) {
        // Select provider based on the PaymentProviderType
        return switch (providerType) {
            case STRIPE -> stripeProvider; // Return the Stripe provider
            case PADDLE -> paddleProvider; // Return the Paddle provider
        };
    }
}
