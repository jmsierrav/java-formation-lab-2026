package com.indra.catalog.suppliers.web;

import com.indra.catalog.suppliers.domain.InvalidSupplierException;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import com.indra.catalog.suppliers.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SupplierNotFoundException exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                Instant.now(), 404, "SUPPLIER_NOT_FOUND", List.of(exception.getMessage()), request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                Instant.now(), 400, "VALIDATION_ERROR", errors, request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected error processing {} {}", request.getMethod(), request.getRequestURI(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                Instant.now(), 500, "INTERNAL_ERROR", List.of("Error interno del servidor"), request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception,
                                                            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "VALIDATION_ERROR",
                List.of("El body debe contener un JSON valido"), request.getRequestURI()));
    }

    @ExceptionHandler(InvalidSupplierException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSupplier(InvalidSupplierException exception,
                                                             HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(), 400, "VALIDATION_ERROR", List.of(exception.getMessage()), request.getRequestURI()));
    }
}
