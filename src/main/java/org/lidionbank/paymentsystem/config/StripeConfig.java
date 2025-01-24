package org.lidionbank.paymentsystem.config;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ConfigProperties(prefix = "quarkus.payment.providers.stripe")
public class StripeConfig {
    @ConfigProperty(name = "enabled")
    public boolean enabled;

    @ConfigProperty(name = "api-key")
    public String apiKey;

    @ConfigProperty(name = "webhook-secret")
    public Optional<String> webhookSecret;

    @ConfigProperty(name = "supported-currencies")
    public List<String> supportedCurrencies;

    @ConfigProperty(name = "supported-countries")
    public List<String> supportedCountries;

    @ConfigProperty(name = "min-amount")
    public BigDecimal minAmount;

    @ConfigProperty(name = "max-amount")
    public BigDecimal maxAmount;
}
