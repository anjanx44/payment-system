package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.lidionbank.paymentsystem.domain.Payment;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.database.PaymentRepository;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProvider;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderFactory paymentProviderFactory;

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public CompletableFuture<PaymentResponse> handlePayment(PaymentRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            PaymentProvider provider = selectPaymentProvider(request);
            if (provider == null) {
                logger.error("No suitable payment provider found for request: {}", request);
                throw new RuntimeException("No suitable payment provider found");
            }

            try {
                PaymentResponse response = provider.processPayment(request);
                savePayment(request, response, provider);
                return response;
            } catch (Exception e) {
                logger.error("Error processing payment for request: {}", request, e);
                throw new RuntimeException("Payment processing failed", e);
            }
        });
    }

    private void savePayment(PaymentRequest request, PaymentResponse response, PaymentProvider provider) {
        Payment payment = new Payment();
        payment.setRequestId(request.getCustomerId()); // Assuming `getCustomerId` is the intended method
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setStatus(response.getStatus().equals("SUCCESS") ? Payment.PaymentStatus.SUCCESS : Payment.PaymentStatus.FAILED);
        payment.setProvider(provider.getClass().getSimpleName());

        paymentRepository.save(payment);
        logger.info("Payment saved: {}", payment);
    }

    private PaymentProvider selectPaymentProvider(PaymentRequest request) {
        String country = request.getCountry();
        switch (country) {
            case "US":
                return paymentProviderFactory.getPaymentProvider("Stripe");
            case "CA":
                return paymentProviderFactory.getPaymentProvider("PayPal");
            // Add more cases as needed
            default:
                return null; // Or a default provider
        }
    }
}