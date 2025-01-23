package org.lidionbank.paymentsystem.infrastructure.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.lidionbank.paymentsystem.domain.PaymentResponse;

import java.util.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.severe("Error processing request: " + exception.getMessage());

        if (exception instanceof PaymentNotFoundException) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(PaymentResponse.failed(exception.getMessage(), null))
                    .build();
        }

        if (exception instanceof ValidationException) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(PaymentResponse.failed(exception.getMessage(), null))
                    .build();
        }

        if (exception instanceof PaymentProcessingException) {
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(PaymentResponse.failed(exception.getMessage(), null))
                    .build();
        }

        // Default error response
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(PaymentResponse.failed("An unexpected error occurred", null))
                .build();
    }
}
