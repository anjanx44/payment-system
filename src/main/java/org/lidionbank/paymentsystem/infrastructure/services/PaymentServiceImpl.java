package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lidionbank.paymentsystem.domain.Payment;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.database.PaymentRepository;
import org.lidionbank.paymentsystem.infrastructure.exception.PaymentNotFoundException;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProvider;
import org.lidionbank.paymentsystem.infrastructure.paymentproviders.PaymentProviderSelector;

import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = Logger.getLogger(PaymentServiceImpl.class.getName());

    @Inject
    PaymentRepository paymentRepository;

    @Inject
    PaymentProviderSelector providerSelector;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Generate a unique payment ID
            UUID paymentId = UUID.randomUUID();

            // Get the appropriate payment provider
            PaymentProvider provider = providerSelector.selectProvider(
                    request.getCountry(),
                    request.getAmount(),
                    request.getCurrency()
            );

            // Create a new payment entity
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerId(request.getCustomerId())
                    .country(request.getCountry())
                    .status(Payment.PaymentStatus.PENDING)
                    .providerName(provider.getProviderName())  // Use provider name instead of enum
                    .build();

            // Save the payment
            try {
                payment = paymentRepository.save(payment);
                LOGGER.info("Payment saved with ID: " + payment.getId());
            } catch (Exception e) {
                LOGGER.severe("Failed to save payment: " + e.getMessage());
                return PaymentResponse.failed("Failed to save payment", generateTransactionId());
            }

            // Process the payment with the selected provider
            PaymentResponse providerResponse = provider.processPayment(request);

            // Generate transaction ID
            String transactionId = generateTransactionId();

            // Return pending response with all required fields
            return PaymentResponse.pending(
                    payment.getId(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getCustomerId(),
                    transactionId
            );

        } catch (Exception e) {
            LOGGER.severe("Error processing payment: " + e.getMessage());
            return PaymentResponse.failed("Payment processing failed: " + e.getMessage(),
                    generateTransactionId());
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Add method to check payment status
    @Transactional
    public PaymentResponse getPaymentStatus(UUID paymentId) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            return PaymentResponse.builder()
                    .paymentId(payment.getId())
                    .status(payment.getStatus().toString())
                    .message(getMessageForStatus(payment.getStatus()))
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .customerId(payment.getCustomerId())
                    .transactionId(generateTransactionId())
                    .timestamp(payment.getCreatedAt())
                    .build();
        } catch (PaymentNotFoundException e) {
            return PaymentResponse.failed("Payment not found", null);
        }
    }

    private String getMessageForStatus(Payment.PaymentStatus status) {
        switch (status) {
            case PENDING:
                return "Payment is being processed";
            case SUCCESS:
                return "Payment processed successfully";
            case FAILED:
                return "Payment processing failed";
            default:
                return "Unknown payment status";
        }
    }
}
