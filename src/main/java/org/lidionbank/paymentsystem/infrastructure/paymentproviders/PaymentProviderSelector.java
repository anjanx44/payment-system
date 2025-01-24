package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * The PaymentProviderSelector class is responsible for selecting the appropriate payment provider
 * based on the country, amount, and currency provided in the payment request. It uses a predefined
 * set of rules to determine which provider to use for each transaction.
 */
@ApplicationScoped
public class PaymentProviderSelector {

    // Injects the list of available payment providers.
    @Inject
    Instance<PaymentProvider> providers;

    /**
     * Selects the appropriate payment provider based on the country, amount, and currency of the payment request.
     *
     * @param country the country where the payment is being processed
     * @param amount the amount to be paid
     * @param currency the currency of the payment
     * @return the selected PaymentProvider
     * @throws IllegalStateException if no suitable provider is found
     */
    public PaymentProvider selectProvider(String country, BigDecimal amount, String currency) {
        // Determine the type of provider based on the payment details
        PaymentProviderType type = selectProviderType(country, amount, currency);

        // Find and return the provider based on the determined provider type
        return providers.stream()
                .filter(provider -> provider.getProviderName().equals(type.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found for type: " + type));
    }

    /**
     * Selects the provider type based on the country, amount, and currency of the payment request.
     * The selection logic involves checking supported countries, currencies, and payment amounts
     * to decide which payment provider to use.
     *
     * @param country the country where the payment is being processed
     * @param amount the amount to be paid
     * @param currency the currency of the payment
     * @return the PaymentProviderType that corresponds to the selected provider
     * @throws UnsupportedOperationException if the country or currency is not supported
     */
    private PaymentProviderType selectProviderType(String country, BigDecimal amount, String currency) {
        // Convert country and currency to uppercase for consistent comparison
        String upperCountry = country.toUpperCase();
        String upperCurrency = currency.toUpperCase();

        // List of supported countries and currencies
        List<String> supportedCountries = Arrays.asList("US", "GB", "DE", "FR", "BD");
        List<String> supportedCurrencies = Arrays.asList("USD", "EUR", "GBP", "BDT");

        // Validate if the country and currency are supported
        if (!supportedCountries.contains(upperCountry) || !supportedCurrencies.contains(upperCurrency)) {
            throw new UnsupportedOperationException(
                    "Payment not supported for country: " + country + " and currency: " + currency
            );
        }

        // Logic for selecting a provider based on the country and amount

        // US transactions: Use Stripe for large transactions, Paddle for smaller ones
        if ("US".equalsIgnoreCase(country)) {
            if (amount.compareTo(new BigDecimal("1000")) > 0) {
                return PaymentProviderType.STRIPE; // Use Stripe for large US transactions
            } else {
                return PaymentProviderType.PADDLE; // Use Paddle for smaller US transactions
            }
        }

        // European transactions: Use Stripe for large transactions, Paddle for smaller ones
        if (Arrays.asList("GB", "DE", "FR").contains(upperCountry)) {
            if (amount.compareTo(new BigDecimal("2000")) > 0) {
                return PaymentProviderType.STRIPE; // Use Stripe for large European transactions
            } else {
                return PaymentProviderType.PADDLE; // Use Paddle for smaller European transactions
            }
        }

        // Bangladesh transactions
        if ("BD".equalsIgnoreCase(country)) {
            // Use Stripe for large BDT transactions, Paddle for smaller ones
            if ("BDT".equalsIgnoreCase(currency)) {
                if (amount.compareTo(new BigDecimal("100000")) > 0) {
                    return PaymentProviderType.STRIPE; // Use Stripe for large BDT transactions
                } else {
                    return PaymentProviderType.PADDLE; // Use Paddle for smaller BDT transactions
                }
            } else {
                // Use Stripe for international transactions from Bangladesh
                return PaymentProviderType.STRIPE;
            }
        }

        // Default: Use Paddle for all other cases
        return PaymentProviderType.PADDLE;
    }

}
