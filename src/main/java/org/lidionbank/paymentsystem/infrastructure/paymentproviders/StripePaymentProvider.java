package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class StripePaymentProvider implements PaymentProvider {
    private static final Logger LOGGER = Logger.getLogger(StripePaymentProvider.class.getName());

    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "DE", "FR");

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            LOGGER.info("Processing payment via Stripe for amount: " + request.getAmount() +
                    " " + request.getCurrency() + " from country: " + request.getCountry());

            // Validate country support
            if (!supports(request)) {
                return new PaymentResponse(
                        "FAILED",
                        "Country not supported by Stripe: " + request.getCountry(),
                        null
                );
            }

            // Simulate external API call
            Thread.sleep(1000);

            String transactionId = "STRIPE_" + UUID.randomUUID().toString();

            return new PaymentResponse(
                    "SUCCESS",
                    "Payment processed successfully via Stripe",
                    transactionId
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Payment processing interrupted: " + e.getMessage());
            return new PaymentResponse(
                    "FAILED",
                    "Payment processing interrupted",
                    null
            );
        } catch (Exception e) {
            LOGGER.severe("Payment processing failed: " + e.getMessage());
            return new PaymentResponse(
                    "FAILED",
                    "Payment processing failed: " + e.getMessage(),
                    null
            );
        }
    }

    @Override
    public boolean supports(PaymentRequest request) {
        if (request == null || request.getCountry() == null) {
            return false;
        }

        // Check if it's a Stripe payment request
        if (!"Stripe".equalsIgnoreCase(request.getProvider())) {
            return false;
        }

        // Support payments from US and Europe
        return SUPPORTED_COUNTRIES.contains(request.getCountry().toUpperCase());
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }
}
