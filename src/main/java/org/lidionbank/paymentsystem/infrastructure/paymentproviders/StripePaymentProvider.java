package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * StripePaymentProvider is a concrete implementation of the PaymentProvider interface,
 * responsible for processing payments through the Stripe API. It supports a predefined set
 * of countries and currencies.
 */
@ApplicationScoped
public class StripePaymentProvider implements PaymentProvider {

    // Logger for logging payment processing actions and errors.
    private static final Logger LOGGER = Logger.getLogger(StripePaymentProvider.class.getName());

    // List of supported countries for Stripe payments.
    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "DE", "FR", "BD");

    // List of supported currencies for Stripe payments.
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("USD", "EUR", "GBP", "BDT");

    // Injecting configuration properties from the application settings.
    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.stripe.enabled", defaultValue = "false")
    boolean stripeEnabled;

    // Injecting the Stripe API key from configuration.
    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.stripe.api-key")
    String apiKey;

    /**
     * Initializes the Stripe API with the provided API key, if Stripe is enabled.
     * This method must be called before using the Stripe payment provider.
     */
    public void init() {
        if (stripeEnabled) {
            Stripe.apiKey = apiKey;
            LOGGER.info("Stripe payment provider initialized");
        }
    }

    /**
     * Processes a payment request using the Stripe payment gateway.
     *
     * @param request the PaymentRequest object containing payment details.
     * @return a PaymentResponse object indicating the success or failure of the payment.
     */
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Check if Stripe payments are enabled
            if (!stripeEnabled) {
                return new PaymentResponse("FAILED", "Stripe payments are not enabled", null);
            }

            LOGGER.info("Processing payment via Stripe for amount: " + request.getAmount() +
                    " " + request.getCurrency() + " from country: " + request.getCountry());

            // Validate if the payment request is supported
            if (!supports(request)) {
                return new PaymentResponse(
                        "FAILED",
                        "Payment not supported by Stripe for this configuration",
                        null
                );
            }

            // Create a PaymentIntent on Stripe
            PaymentIntent paymentIntent = createPaymentIntent(request);

            // Return success response with the Stripe PaymentIntent ID
            return new PaymentResponse(
                    "SUCCESS",
                    "Payment processed successfully via Stripe",
                    "STRIPE_" + paymentIntent.getId()
            );

        } catch (Exception e) {
            // Log the error and return a failure response
            LOGGER.severe("Payment processing failed: " + e.getMessage());
            return new PaymentResponse(
                    "FAILED",
                    "Payment processing failed: " + e.getMessage(),
                    null
            );
        }
    }

    /**
     * Creates a PaymentIntent on Stripe using the details from the payment request.
     *
     * @param request the PaymentRequest object containing payment details.
     * @return a PaymentIntent object created by Stripe.
     * @throws Exception if an error occurs while creating the PaymentIntent.
     */
    private PaymentIntent createPaymentIntent(PaymentRequest request) throws Exception {
        // Build the PaymentIntent parameters, converting the amount to cents
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount().multiply(new BigDecimal("100")).longValue()) // Convert to cents
                .setCurrency(request.getCurrency().toLowerCase())  // Set the currency in lowercase (Stripe format)
                .setDescription("Payment for customer: " + request.getCustomerId())
                .putMetadata("customerId", request.getCustomerId())  // Attach metadata for later reference
                .putMetadata("country", request.getCountry())  // Attach the country metadata
                .setConfirm(false)  // Don't confirm the payment yet (just create the intent)
                .build();

        // Create and return the PaymentIntent from Stripe
        return PaymentIntent.create(params);
    }

    /**
     * Determines if the given payment request is supported by Stripe.
     *
     * @param request the PaymentRequest object containing payment details.
     * @return true if the payment request is supported, false otherwise.
     */
    @Override
    public boolean supports(PaymentRequest request) {
        if (!stripeEnabled || request == null) {
            return false;
        }

        // Check if the country is supported by Stripe
        boolean isCountrySupported = request.getCountry() != null &&
                SUPPORTED_COUNTRIES.contains(request.getCountry().toUpperCase());

        // Check if the currency is supported by Stripe
        boolean isCurrencySupported = request.getCurrency() != null &&
                SUPPORTED_CURRENCIES.contains(request.getCurrency().toUpperCase());

        // Ensure the payment amount is valid (greater than zero)
        boolean isValidAmount = isValidAmount(request);

        return isCountrySupported && isCurrencySupported && isValidAmount;
    }

    /**
     * Validates that the payment amount is greater than zero.
     *
     * @param request the PaymentRequest object containing payment details.
     * @return true if the amount is valid (greater than zero), false otherwise.
     */
    private boolean isValidAmount(PaymentRequest request) {
        return request.getAmount() != null &&
                request.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Returns the name of the payment provider.
     *
     * @return the name of the provider (Stripe).
     */
    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    /**
     * Returns a list of countries supported by Stripe for payments.
     *
     * @return a list of supported countries.
     */
    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    /**
     * Returns a list of currencies supported by Stripe for payments.
     *
     * @return a list of supported currencies.
     */
    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
