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

/**
 * The PaymentServiceImpl class implements the PaymentService interface and is responsible
 * for handling payment-related operations, including processing payments, saving payment records,
 * and retrieving payment statuses.
 */
@ApplicationScoped
public class PaymentServiceImpl implements PaymentService {

    // Logger for logging various actions and errors related to payment processing.
    private static final Logger LOGGER = Logger.getLogger(PaymentServiceImpl.class.getName());

    // Injecting PaymentRepository to interact with the database and save payment records.
    @Inject
    PaymentRepository paymentRepository;

    // Injecting PaymentProviderSelector to select the appropriate payment provider based on the request.
    @Inject
    PaymentProviderSelector providerSelector;

    /**
     * Processes a payment request by selecting the appropriate payment provider,
     * saving the payment details, and processing the payment.
     *
     * @param request the payment request containing the payment details (amount, currency, customer, etc.).
     * @return a PaymentResponse object with the status and details of the processed payment.
     */
    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Generate a unique payment ID for this transaction.
            UUID paymentId = UUID.randomUUID();

            // Select the appropriate payment provider based on the payment request details.
            PaymentProvider provider = providerSelector.selectProvider(
                    request.getCountry(),    // Country of the customer making the payment.
                    request.getAmount(),     // Payment amount.
                    request.getCurrency()    // Currency of the payment.
            );

            // Create a new Payment entity to represent this transaction.
            Payment payment = Payment.builder()
                    .id(paymentId)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerId(request.getCustomerId())
                    .country(request.getCountry())
                    .status(Payment.PaymentStatus.PENDING)  // Initially set payment status to PENDING.
                    .providerName(provider.getProviderName())  // Use provider's name instead of an enum.
                    .build();

            // Attempt to save the payment entity in the database.
            try {
                payment = paymentRepository.save(payment);
                LOGGER.info("Payment saved with ID: " + payment.getId());
            } catch (Exception e) {
                // If saving fails, log the error and return a failed response.
                LOGGER.severe("Failed to save payment: " + e.getMessage());
                return PaymentResponse.failed("Failed to save payment", generateTransactionId());
            }

            // Process the payment using the selected provider.
            PaymentResponse providerResponse = provider.processPayment(request);

            // Generate a unique transaction ID for this payment.
            String transactionId = generateTransactionId();

            // Return a pending payment response with the relevant details.
            return PaymentResponse.pending(
                    payment.getId(),       // Payment ID
                    payment.getAmount(),   // Payment amount
                    payment.getCurrency(), // Payment currency
                    payment.getCustomerId(), // Customer ID
                    transactionId          // Generated transaction ID
            );

        } catch (Exception e) {
            // If any error occurs during payment processing, log the error and return a failed response.
            LOGGER.severe("Error processing payment: " + e.getMessage());
            return PaymentResponse.failed("Payment processing failed: " + e.getMessage(), generateTransactionId());
        }
    }

    /**
     * Generates a unique transaction ID for each payment.
     *
     * @return a string representing the unique transaction ID.
     */
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Retrieves the status of a payment by its ID.
     *
     * @param paymentId the unique identifier of the payment.
     * @return a PaymentResponse containing the payment status and details.
     */
    @Transactional
    public PaymentResponse getPaymentStatus(UUID paymentId) {
        try {
            // Find the payment in the repository by its ID.
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            // Return a response containing the payment status and relevant details.
            return PaymentResponse.builder()
                    .paymentId(payment.getId())                    // Payment ID
                    .status(payment.getStatus().toString())         // Payment status
                    .message(getMessageForStatus(payment.getStatus())) // Status message
                    .amount(payment.getAmount())                    // Payment amount
                    .currency(payment.getCurrency())                // Payment currency
                    .customerId(payment.getCustomerId())            // Customer ID
                    .transactionId(generateTransactionId())        // Generated transaction ID
                    .timestamp(payment.getCreatedAt())              // Timestamp of when payment was created
                    .build();
        } catch (PaymentNotFoundException e) {
            // If the payment is not found, return a failed response.
            return PaymentResponse.failed("Payment not found", null);
        }
    }

    /**
     * Returns a message corresponding to the current status of the payment.
     *
     * @param status the current status of the payment.
     * @return a message describing the payment's status.
     */
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
