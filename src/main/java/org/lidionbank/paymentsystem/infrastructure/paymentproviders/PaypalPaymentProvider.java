package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class PaypalPaymentProvider implements PaymentProvider {
    private static final Logger LOGGER = Logger.getLogger(PaypalPaymentProvider.class.getName());

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paypal.client-id")
    String clientId;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paypal.client-secret")
    String clientSecret;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Your existing implementation
            LOGGER.info("Processing payment via PayPal for amount: " + request.getAmount() +
                    " " + request.getCurrency());

            // Simulate external API call
            Thread.sleep(1000);

            if (!isValidPayment(request)) {
                return new PaymentResponse("FAILED", "Invalid payment details", null);
            }

            String transactionId = "PP_" + UUID.randomUUID().toString();

            return new PaymentResponse("SUCCESS", "Payment processed successfully via PayPal", transactionId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.severe("Payment processing interrupted: " + e.getMessage());
            return new PaymentResponse("FAILED", "Payment processing interrupted", null);
        } catch (Exception e) {
            LOGGER.severe("Payment processing failed: " + e.getMessage());
            return new PaymentResponse("FAILED", "Payment processing failed: " + e.getMessage(), null);
        }
    }

    @Override
    public boolean supports(PaymentRequest request) {
        return "PayPal".equalsIgnoreCase(request.getProvider());
    }

    @Override
    public String getProviderName() {
        return "PAYPAL";
    }

    private boolean isValidPayment(PaymentRequest request) {
        // Your existing validation logic
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            LOGGER.warning("Invalid payment amount");
            return false;
        }

        if (request.getCurrency() == null || request.getCurrency().trim().isEmpty()) {
            LOGGER.warning("Invalid currency");
            return false;
        }

        if (request.getCustomerId() == null || request.getCustomerId().trim().isEmpty()) {
            LOGGER.warning("Invalid customer ID");
            return false;
        }

        return true;
    }
}
