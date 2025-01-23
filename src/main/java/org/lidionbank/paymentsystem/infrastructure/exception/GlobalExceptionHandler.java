package org.lidionbank.paymentsystem.infrastructure.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.lidionbank.paymentsystem.domain.ErrorResponse;

import java.util.logging.Logger;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        LOGGER.severe("Unhandled exception: " + exception.getMessage());

        ErrorResponse error = ErrorResponse.internalError(
                "An unexpected error occurred",
                uriInfo.getPath()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}
