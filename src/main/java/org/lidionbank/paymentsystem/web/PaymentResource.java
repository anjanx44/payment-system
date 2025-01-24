package org.lidionbank.paymentsystem.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.lidionbank.paymentsystem.domain.ErrorResponse;
import org.lidionbank.paymentsystem.domain.Payment;
import org.lidionbank.paymentsystem.domain.PaymentRequest;
import org.lidionbank.paymentsystem.domain.PaymentResponse;
import org.lidionbank.paymentsystem.infrastructure.database.PaymentRepository;
import org.lidionbank.paymentsystem.infrastructure.services.PaymentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/api/v1/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {
    private static final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());

    @Inject
    PaymentService paymentService;

    @Inject
    PaymentRepository paymentRepository;

    @Context
    UriInfo uriInfo;

    @POST
    public Response createPayment(@Valid @NotNull PaymentRequest request) {
        try {
            LOGGER.info("Received payment request for amount: " + request.getAmount() +
                    " " + request.getCurrency() + " from country: " + request.getCountry());

            if (!isValidPaymentRequest(request)) {
                ErrorResponse error = ErrorResponse.badRequest(
                        "Invalid payment request",
                        uriInfo.getPath()
                );
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            PaymentResponse response = paymentService.processPayment(request);
            LOGGER.info("Payment processing initiated with status: " + response.getStatus());

            return Response.status(Response.Status.ACCEPTED)
                    .entity(response)
                    .build();

        } catch (Exception e) {
            LOGGER.severe("Error processing payment: " + e.getMessage());
            ErrorResponse error = ErrorResponse.internalError(
                    "Error processing payment",
                    uriInfo.getPath()
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getPayment(@PathParam("id") UUID id) {
        try {
            LOGGER.info("Fetching payment details for ID: " + id);

            if (id == null) {
                ErrorResponse error = ErrorResponse.badRequest(
                        "Payment ID cannot be null",
                        uriInfo.getPath()
                );
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            Optional<Payment> payment = paymentRepository.findById(id);

            if (payment.isEmpty()) {
                LOGGER.warning("Payment not found for ID: " + id);
                ErrorResponse error = ErrorResponse.notFound(
                        "Payment not found",
                        uriInfo.getPath()
                );
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            return Response.ok(payment).build();

        } catch (Exception e) {
            LOGGER.severe("Error fetching payment: " + e.getMessage());
            ErrorResponse error = ErrorResponse.internalError(
                    "Error fetching payment details",
                    uriInfo.getPath()
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    @GET
    public Response getAllPayments(@QueryParam("status") String status,
                                   @QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("20") int size) {
        try {
            LOGGER.info("Fetching payments with status: " + status + ", page: " + page);

            List<Payment> payments;
            if (status != null && !status.isEmpty()) {
                try {
                    Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status.toUpperCase());
                    payments = paymentRepository.findByStatus(paymentStatus);
                } catch (IllegalArgumentException e) {
                    ErrorResponse error = ErrorResponse.badRequest(
                            "Invalid payment status",
                            uriInfo.getPath()
                    );
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(error)
                            .build();
                }
            } else {
                payments = paymentRepository.findAll(page, size);
            }

            return Response.ok(payments).build();

        } catch (Exception e) {
            LOGGER.severe("Error fetching payments: " + e.getMessage());
            ErrorResponse error = ErrorResponse.internalError(
                    "Error fetching payments",
                    uriInfo.getPath()
            );
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error)
                    .build();
        }
    }

    private boolean isValidPaymentRequest(PaymentRequest request) {
        return request != null &&
                request.getAmount() != null &&
                request.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                request.getCurrency() != null &&
                !request.getCurrency().trim().isEmpty() &&
                request.getCustomerId() != null &&
                !request.getCustomerId().trim().isEmpty() &&
                request.getCountry() != null &&
                !request.getCountry().trim().isEmpty();
    }


}
