payment-system/
├── src/main/java/
│   ├── com/example/paymentsystem/
│   │   ├── config/
│   │   │   ├── ApplicationConfig.java
│   │   │   ├── DatabaseConfig.java
│   │   │   ├── PaymentProviderConfig.java
│   │   ├── domain/
│   │   │   ├── Payment.java
│   │   │   ├── PaymentRequest.java
│   │   │   ├── PaymentResponse.java
│   │   │   ├── PaymentStatus.java
│   │   ├── infrastructure/
│   │   │   ├── database/
│   │   │   │   ├── PaymentRepository.java
│   │   │   ├── paymentproviders/
│   │   │   │   ├── PaymentProvider.java
│   │   │   │   ├── StripePaymentProvider.java
│   │   │   │   ├── PaypalPaymentProvider.java
│   │   │   ├── services/
│   │   │   │   ├── PaymentService.java
│   │   ├── resources/
│   │   │   ├── PaymentResource.java
│   ├── META-INF/
│   │   ├── services/
│   │   │   ├── org/eclipse/microprofile/config/
│   │   │   │   ├── microprofile-config.properties
│   ├── resources/
│   │   ├── application.properties
│   ├── resources/db/migration/
│   │   ├── V1__init.sql
├── src/test/java/
│   ├── com/example/paymentsystem/
│   │   ├── unit/
│   │   │   ├── PaymentServiceTest.java
│   ├── resources/
│   │   ├── application.properties
└── Dockerfile