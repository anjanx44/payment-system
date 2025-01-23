//package org.lidionbank.paymentsystem.infrastructure.paymentproviders;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import org.lidionbank.paymentsystem.domain.PaymentRequest;
//import org.lidionbank.paymentsystem.domain.PaymentResponse;
//
//import java.util.UUID;
//
//@ApplicationScoped
//public class NagadPaymentProvider implements PaymentProvider {
//    private final String merchantId;
//    private final String merchantKey;
//    private final String publicKey;
//
//    public NagadPaymentProvider(
//            @ConfigProperty(name = "payment.nagad.merchant-id") String merchantId,
//            @ConfigProperty(name = "payment.nagad.merchant-key") String merchantKey,
//            @ConfigProperty(name = "payment.nagad.public-key") String publicKey) {
//        this.merchantId = merchantId;
//        this.merchantKey = merchantKey;
//        this.publicKey = publicKey;
//    }
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request) {
//        // Implement Nagad payment logic
//        return PaymentResponse.builder()
//                .transactionId("nagad_" + UUID.randomUUID().toString())
//                .status("SUCCESS")
//                .provider("Nagad")
//                .amount(request.getAmount())
//                .build();
//    }
//}
//
