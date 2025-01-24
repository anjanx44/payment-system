package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProvider;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProviderSelector;

import java.util.logging.Logger;

@ApplicationScoped
public class PaymentProcessor {
    private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class.getName());

    @Inject
    PaymentProviderSelector providerSelector;

    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            if (request == null) {
                LOGGER.warning("Payment request is null");
                return new PaymentResponse("FAILED", "Invalid payment request", null);
            }

            // Get provider using selector instead of factory
            PaymentProvider provider = providerSelector.selectProvider(
                    request.getCountry(),
                    request.getAmount(),
                    request.getCurrency()
            );

            if (provider == null) {
                LOGGER.severe("No suitable payment provider found");
                return new PaymentResponse("FAILED", "Payment provider not available", null);
            }

            if (!provider.supports(request)) {
                LOGGER.warning("Payment provider does not support this request");
                return new PaymentResponse("FAILED", "Payment method not supported", null);
            }

            LOGGER.info("Processing payment with provider: " + provider.getProviderName());
            return provider.processPayment(request);

        } catch (IllegalArgumentException e) {
            LOGGER.severe("Invalid payment request: " + e.getMessage());
            return new PaymentResponse("FAILED", "Invalid payment request", null);
        } catch (Exception e) {
            LOGGER.severe("Payment processing error: " + e.getMessage());
            return new PaymentResponse("FAILED", "Payment processing failed", null);
        }
    }
}
