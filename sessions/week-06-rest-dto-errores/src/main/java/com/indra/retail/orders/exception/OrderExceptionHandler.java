package com.indra.retail.orders.exception;

import com.indra.retail.orders.dto.ErrorResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestControllerAdvice
public class OrderExceptionHandler {

    private final MessageSource messageSource;

    public OrderExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                List.of(messageSource.getMessage(
                        "api.error.order-not-found",
                        new Object[]{ex.getOrderId()},
                        LocaleContextHolder.getLocale()
                ))
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> messageSource.getMessage(
                        "api.error.validation",
                        new Object[]{
                                messageSource.getMessage(
                                        "api.field." + error.getField().replaceAll("\\[\\d+\\]", "[]"),
                                        null,
                                        error.getField(),
                                        LocaleContextHolder.getLocale()
                                ),
                                error.getDefaultMessage()
                        },
                        LocaleContextHolder.getLocale()
                ))
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of(messageSource.getMessage(
                        "api.error.internal",
                        null,
                        LocaleContextHolder.getLocale()
                ))
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, List<String> errors) {
        var response = new ErrorResponse(
                LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                errors
        );

        return ResponseEntity.status(status).body(response);
    }

}
