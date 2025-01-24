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

@ApplicationScoped
public class StripePaymentProvider implements PaymentProvider {
    private static final Logger LOGGER = Logger.getLogger(StripePaymentProvider.class.getName());

    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "DE", "FR", "BD");
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("USD", "EUR", "GBP", "BDT");

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.stripe.enabled", defaultValue = "false")
    boolean stripeEnabled;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.stripe.api-key")
    String apiKey;

    public void init() {
        if (stripeEnabled) {
            Stripe.apiKey = apiKey;
            LOGGER.info("Stripe payment provider initialized");
        }
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            if (!stripeEnabled) {
                return new PaymentResponse("FAILED", "Stripe payments are not enabled", null);
            }

            LOGGER.info("Processing payment via Stripe for amount: " + request.getAmount() +
                    " " + request.getCurrency() + " from country: " + request.getCountry());

            if (!supports(request)) {
                return new PaymentResponse(
                        "FAILED",
                        "Payment not supported by Stripe for this configuration",
                        null
                );
            }

            // Create Stripe Payment Intent
            PaymentIntent paymentIntent = createPaymentIntent(request);

            return new PaymentResponse(
                    "SUCCESS",
                    "Payment processed successfully via Stripe",
                    "STRIPE_" + paymentIntent.getId()
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

    private PaymentIntent createPaymentIntent(PaymentRequest request) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount().multiply(new BigDecimal("100")).longValue()) // Convert to cents
                .setCurrency(request.getCurrency().toLowerCase())
                .setDescription("Payment for customer: " + request.getCustomerId())
                .putMetadata("customerId", request.getCustomerId())
                .putMetadata("country", request.getCountry())
                .setConfirm(false)
                .build();

        return PaymentIntent.create(params);
    }

    @Override
    public boolean supports(PaymentRequest request) {
        if (!stripeEnabled || request == null) {
            return false;
        }

        boolean isCountrySupported = request.getCountry() != null &&
                SUPPORTED_COUNTRIES.contains(request.getCountry().toUpperCase());

        boolean isCurrencySupported = request.getCurrency() != null &&
                SUPPORTED_CURRENCIES.contains(request.getCurrency().toUpperCase());

        boolean isValidAmount = isValidAmount(request);

        return isCountrySupported && isCurrencySupported && isValidAmount;
    }

    private boolean isValidAmount(PaymentRequest request) {
        return request.getAmount() != null &&
                request.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String getProviderName() {
        return "STRIPE";
    }

    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
