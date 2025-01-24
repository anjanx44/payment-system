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

    private static final List<String> SUPPORTED_COUNTRIES = Arrays.asList("US", "GB", "DE", "FR", "BD");
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("USD", "EUR", "GBP", "BDT");

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.enabled", defaultValue = "false")
    boolean paddleEnabled;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.vendor-id")
    String vendorId;

    @Inject
    @ConfigProperty(name = "quarkus.payment.providers.paddle.api-key")
    String apiKey;

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

            // Create Paddle Payment Link
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

    private String createPaddlePaymentLink(PaymentRequest request) throws Exception {
        // Make a HTTP POST request to Paddle's /2.0/payment/links endpoint
        String paddleApiUrl = "https://vendors.paddle.com/api/2.0/payment/links";

        // Construct payload
        String payload = String.format(
                "vendor_id=%s&vendor_auth_code=%s&title=%s&amount=%s&currency=%s&return_url=%s",
                vendorId,
                apiKey,
                "Payment for Customer " + request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                "https://your-return-url.com"
        );

        // Send HTTP request (use any HTTP client like WebClient, OkHttp, etc.)
        // Example using Java's HttpClient:
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

        // Parse response (assuming JSON response)
        // Extract "url" from the response JSON
        String paymentLink = extractPaymentLink(response.body());
        LOGGER.info("Payment link created: " + paymentLink);

        return paymentLink;
    }

    private String extractPaymentLink(String responseBody) {
        // Use a JSON library to extract the payment link from the response
        // Example with Jackson:
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
            return jsonNode.get("response").get("url").asText();
        } catch (Exception e) {
            LOGGER.severe("Failed to parse payment link from Paddle response: " + e.getMessage());
            return null;
        }
    }

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

    private boolean isValidAmount(PaymentRequest request) {
        return request.getAmount() != null &&
                request.getAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String getProviderName() {
        return "PADDLE";
    }

    public List<String> getSupportedCountries() {
        return SUPPORTED_COUNTRIES;
    }

    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
}
