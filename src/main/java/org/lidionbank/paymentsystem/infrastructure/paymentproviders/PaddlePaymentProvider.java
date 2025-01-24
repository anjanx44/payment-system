package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

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
public class PaddlePaymentProvider implements PaymentProvider {

    private static final Logger LOGGER = Logger.getLogger(PaddlePaymentProvider.class.getName());

    // Supported countries and currencies for Paddle payments
    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "DE", "FR", "BD");
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("USD", "EUR", "GBP", "BDT");

    // Configuration properties for Paddle payment provider
    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.enabled", defaultValue = "false")
    boolean paddleEnabled;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.vendor-id")
    String vendorId;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.api-key")
    String apiKey;

    /**
     * Processes the payment request via Paddle. If Paddle payments are enabled and the request is valid,
     * a payment link is generated and returned as part of the payment response.
     *
     * @param request The payment request containing the necessary details.
     * @return The payment response with the result of the payment processing.
     */
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            if (!paddleEnabled) {
                return new PaymentResponse("FAILED", "Paddle payments are not enabled", null);
            }

            LOGGER.info("Processing payment via Paddle for amount: " + request.getAmount() +
                    " " + request.getCurrency() + " from country: " + request.getCountry());

            if (!supports(request)) {
                return new PaymentResponse(
                        "FAILED",
                        "Payment not supported by Paddle for this configuration",
                        null
                );
            }

            // Create Paddle payment link
            String paymentLink = createPaddlePaymentLink(request);

            return new PaymentResponse(
                    "SUCCESS",
                    "Payment link generated successfully via Paddle",
                    "PADDLE_" + paymentLink
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

    /**
     * Creates a payment link using Paddle's payment API.
     *
     * @param request The payment request containing the necessary information.
     * @return The generated payment link as a string.
     * @throws Exception If the payment link creation fails.
     */
    private String createPaddlePaymentLink(PaymentRequest request) throws Exception {
        // Paddle API URL for creating payment links
        String paddleApiUrl = "https://vendors.paddle.com/api/2.0/payment/links";

        // Construct the payload to send in the request
        String payload = String.format(
                "vendor_id=%s&vendor_auth_code=%s&title=%s&amount=%s&currency=%s&return_url=%s",
                vendorId,
                apiKey,
                "Payment for Customer " + request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                "https://your-return-url.com"
        );

        // Send HTTP request to Paddle API to create payment link
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(paddleApiUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload))
                .build();

        java.net.http.HttpResponse<String> response = client.send(httpRequest, java.net.http.HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Failed to create Paddle payment link: " + response.body());
        }

        // Parse the response and extract the payment link
        return extractPaymentLink(response.body());
    }

    /**
     * Extracts the payment link from the Paddle API response.
     *
     * @param responseBody The response body from the Paddle API.
     * @return The extracted payment link.
     */
    private String extractPaymentLink(String responseBody) {
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
            return jsonNode.get("response").get("url").asText();
        } catch (Exception e) {
            LOGGER.severe("Failed to parse payment link from Paddle response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if the payment request is supported by Paddle.
     * A request is supported if the country, currency, and amount meet the provider's requirements.
     *
     * @param request The payment request to validate.
     * @return True if the payment request is supported, false otherwise.
     */
    @Override
    public boolean supports(PaymentRequest request) {
        if (!paddleEnabled || request == null) {
            return false;
        }

        boolean isCountrySupported = request.getCountry() != null &&
                SUPPORTED_COUNTRIES.contains(request.getCountry().toUpperCase());

        boolean isCurrencySupported = request.getCurrency() != null &&
                SUPPORTED_CURRENCIES.contains(request.getCurrency().toUpperCase());

        boolean isValidAmount = isValidAmount(request);

        return isCountrySupported && isCurrencySupported && isValidAmount;
    }

    /**
     * Validates if the amount in the payment request is greater than zero.
     *
     * @param request The payment request containing the amount to validate.
     * @return True if the amount is valid (greater than zero), false otherwise.
     */
    private boolean isValidAmount(PaymentRequest request) {
        return request.getAmount() != null &&
                request.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Retrieves the name of the payment provider.
     *
     * @return The name of the provider (PADDLE).
     */
    @Override
    public String getProviderName() {
        return "PADDLE";
    }

    /**
     * Retrieves the list of supported countries by Paddle.
     *
     * @return The list of supported countries.
     */
    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    /**
     * Retrieves the list of supported currencies by Paddle.
     *
     * @return The list of supported currencies.
     */
    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
