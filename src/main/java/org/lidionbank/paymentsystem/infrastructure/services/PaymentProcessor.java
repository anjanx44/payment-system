package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProvider;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProviderFactory;

import java.util.logging.Logger;

@ApplicationScoped
public class PaymentProcessor {
    private static final Logger LOGGER = Logger.getLogger(PaymentProcessor.class.getName());

    @Inject
    PaymentProviderFactory paymentProviderFactory;

    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            if (request == null) {
                LOGGER.warning("Payment request is null");
                return new PaymentResponse("FAILED", "Invalid payment request", null);
            }

            PaymentProvider provider = paymentProviderFactory.getPaymentProvider(request.getProvider());
            if (provider == null) {
                LOGGER.severe("Payment provider not found: " + request.getProvider());
                return new PaymentResponse("FAILED", "Payment provider not available", null);
            }

            if (!provider.supports(request)) {
                LOGGER.warning("Payment provider does not support this request");
                return new PaymentResponse("FAILED", "Payment method not supported", null);
            }

            return provider.processPayment(request);

        } catch (IllegalArgumentException e) {
            LOGGER.severe("Invalid payment provider: " + e.getMessage());
            return new PaymentResponse("FAILED", "Invalid payment provider", null);
        } catch (Exception e) {
            LOGGER.severe("Payment processing error: " + e.getMessage());
            return new PaymentResponse("FAILED", "Payment processing failed", null);
        }
    }
}
