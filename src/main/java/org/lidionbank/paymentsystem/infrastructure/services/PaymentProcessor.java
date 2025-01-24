package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProvider;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProviderSelector;

import java.util.logging.Logger;

/**
 * The PaymentProcessor class is responsible for processing payment requests by selecting
 * the appropriate payment provider and executing the payment process.
 */
@ApplicationScoped
public class PaymentProcessor {

    // Logger for logging various information related to payment processing.
    private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class.getName());

    // Injecting PaymentProviderSelector which is responsible for selecting a suitable payment provider.
    @Inject
    PaymentProviderSelector providerSelector;

    /**
     * Processes a payment request by selecting an appropriate payment provider and processing the payment.
     *
     * @param request the payment request containing the payment details (e.g., amount, currency, country).
     * @return a PaymentResponse object containing the status and details of the payment.
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Validate the incoming payment request.
            if (request == null) {
                LOGGER.warning("Payment request is null");
                return new PaymentResponse("FAILED", "Invalid payment request", null);
            }

            // Select the payment provider based on the payment request details.
            PaymentProvider provider = providerSelector.selectProvider(
                    request.getCountry(),   // Country in which the payment is to be processed.
                    request.getAmount(),    // Amount to be processed.
                    request.getCurrency()   // Currency of the payment.
            );

            // If no suitable provider is found, return a failed response.
            if (provider == null) {
                LOGGER.severe("No suitable payment provider found");
                return new PaymentResponse("FAILED", "Payment provider not available", null);
            }

            // Check if the selected provider supports the requested payment.
            if (!provider.supports(request)) {
                LOGGER.warning("Payment provider does not support this request");
                return new PaymentResponse("FAILED", "Payment method not supported", null);
            }

            // Log the provider name for debugging or tracking purposes.
            LOGGER.info("Processing payment with provider: " + provider.getProviderName());

            // Process the payment using the selected provider and return the result.
            return provider.processPayment(request);

        } catch (IllegalArgumentException e) {
            // Handle any illegal argument exceptions and log the error.
            LOGGER.severe("Invalid payment request: " + e.getMessage());
            return new PaymentResponse("FAILED", "Invalid payment request", null);
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log the error.
            LOGGER.severe("Payment processing error: " + e.getMessage());
            return new PaymentResponse("FAILED", "Payment processing failed", null);
        }
    }
}
