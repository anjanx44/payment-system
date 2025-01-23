package org.lidionbank.paymentsystem.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.services.PaymentService;

import java.util.concurrent.CompletableFuture;
@Path("/payments")
public class PaymentResource {

    @Inject
    PaymentService paymentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPayment(PaymentRequest request) {
        CompletableFuture<PaymentResponse> futureResponse = paymentService.handlePayment(request);

        // Optionally handle the response or log it here
        futureResponse.thenAccept(response -> {
            // Handle success (e.g., log the result)
        }).exceptionally(ex -> {
            // Handle error (e.g., log the exception)
            return null;
        });

        return Response.status(Response.Status.ACCEPTED)
                .entity("Payment is being processed.")
                .build();
    }
}