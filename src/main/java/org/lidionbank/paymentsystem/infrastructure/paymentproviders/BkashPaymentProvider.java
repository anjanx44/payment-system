//package org.lidionbank.paymentsystem.infrastructure.paymentproviders;
//
//import jakarta.enterprise.context.ApplicationScoped;
//import org.lidionbank.paymentsystem.domain.PaymentRequest;
//import org.lidionbank.paymentsystem.domain.PaymentResponse;
//
//import java.util.UUID;
//
//@ApplicationScoped
//public class BkashPaymentProvider implements PaymentProvider {
//    private final String username;
//    private final String password;
//    private final String appKey;
//    private final String appSecret;
//
//    public BkashPaymentProvider(
//            @ConfigProperty(name = "payment.bkash.username") String username,
//            @ConfigProperty(name = "payment.bkash.password") String password,
//            @ConfigProperty(name = "payment.bkash.app-key") String appKey,
//            @ConfigProperty(name = "payment.bkash.app-secret") String appSecret) {
//        this.username = username;
//        this.password = password;
//        this.appKey = appKey;
//        this.appSecret = appSecret;
//    }
//
//    @Override
//    public PaymentResponse processPayment(PaymentRequest request) {
//        // Implement bKash payment logic
//        return PaymentResponse.builder()
//                .transactionId("bkash_" + UUID.randomUUID().toString())
//                .status("SUCCESS")
//                .provider("bKash")
//                .amount(request.getAmount())
//                .build();
//    }
//}
//
