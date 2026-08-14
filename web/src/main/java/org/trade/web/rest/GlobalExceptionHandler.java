package org.trade.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.trade.core.exception.ModelException;
import org.trade.core.properties.MissingPropertiesException;
import org.trade.core.properties.PropertyFileNotFoundException;
import org.trade.core.properties.PropertyNotFoundException;
import org.trade.web.rest.response.ErrorResponse;

import java.io.IOException;

@RestControllerAdvice // Automatically catches exceptions thrown by any @RestController
public class GlobalExceptionHandler {

    // Handles a specific custom exception
    @ExceptionHandler(MissingPropertiesException.class)
    public ResponseEntity<ErrorResponse> missingProperties(MissingPropertiesException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PropertyFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> propertyFileNotFound(PropertyFileNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ErrorResponse> propertyNotFound(PropertyNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Fallback handler for any other unhandled runtime exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred.",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}