package org.lidionbank.paymentsystem.infrastructure.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lidionbank.paymentsystem.domain.Payment;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.database.PaymentRepository;

import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = Logger.getLogger(PaymentServiceImpl.class.getName());

    @Inject
    PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Generate a unique payment ID
            UUID paymentId = UUID.randomUUID();

            // Create a new payment entity
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerId(request.getCustomerId())
                    .status(Payment.PaymentStatus.PENDING)
                    .build();

            // Save the payment
            payment = paymentRepository.save(payment);

            // Generate transaction ID
            String transactionId = generateTransactionId();

            // Return response
            return PaymentResponse.pending(
                    payment.getId(),
                    payment.getAmount(),
                    payment.getCurrency(),
                    payment.getCustomerId(),
                    transactionId
            );

        } catch (Exception e) {
            LOGGER.severe("Error processing payment: " + e.getMessage());
            return PaymentResponse.failed("Payment processing failed", null);
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
