package org.lidionbank.paymentsystem.infrastructure.paymentproviders;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class PaymentProviderSelector {

    @Inject
    Instance<PaymentProvider> providers;

    public PaymentProvider selectProvider(String country, BigDecimal amount, String currency) {
        PaymentProviderType type = selectProviderType(country, amount, currency);

        return providers.stream()
                .filter(provider -> provider.getProviderName().equals(type.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No provider found for type: " + type));
    }

    private PaymentProviderType selectProviderType(String country, BigDecimal amount, String currency) {
        // Convert country to uppercase for consistent comparison
        String upperCountry = country.toUpperCase();
        String upperCurrency = currency.toUpperCase();

        // List of supported countries and currencies
        List<String> supportedCountries = Arrays.asList("US", "GB", "DE", "FR", "BD");
        List<String> supportedCurrencies = Arrays.asList("USD", "EUR", "GBP", "BDT");

        // Validate country and currency support
        if (!supportedCountries.contains(upperCountry) ||
                !supportedCurrencies.contains(upperCurrency)) {
//            LOGGER.warning("Unsupported country or currency: " + upperCountry + "/" + upperCurrency);
            throw new UnsupportedOperationException(
                    "Payment not supported for country: " + country + " and currency: " + currency
            );
        }

        // US transactions
        if ("US".equalsIgnoreCase(country)) {
            if (amount.compareTo(new BigDecimal("1000")) > 0) {
                return PaymentProviderType.STRIPE; // Stripe for large US transactions
            } else {
                return PaymentProviderType.PADDLE; // Paddle for smaller US transactions
            }
        }

        // European transactions
        if (Arrays.asList("GB", "DE", "FR").contains(upperCountry)) {
            if (amount.compareTo(new BigDecimal("2000")) > 0) {
                return PaymentProviderType.STRIPE; // Stripe for large European transactions
            } else {
                return PaymentProviderType.PADDLE; // Paddle for smaller European transactions
            }
        }

        // Bangladesh transactions
        if ("BD".equalsIgnoreCase(country)) {
            if ("BDT".equalsIgnoreCase(currency)) {
                if (amount.compareTo(new BigDecimal("100000")) > 0) {
                    return PaymentProviderType.STRIPE; // Stripe for large BDT transactions
                } else {
                    return PaymentProviderType.PADDLE; // Paddle for smaller BDT transactions
                }
            } else {
                // For international currencies from Bangladesh
                return PaymentProviderType.STRIPE;
            }
        }

        // Default to Paddle for other supported countries/smaller amounts
        return PaymentProviderType.PADDLE;
    }

}
