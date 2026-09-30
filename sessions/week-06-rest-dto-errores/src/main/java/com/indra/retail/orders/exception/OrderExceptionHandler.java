package com.indra.retail.orders.exception;

import com.indra.retail.orders.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestControllerAdvice
public class OrderExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderExceptionHandler.class);

    private final MessageSource messageSource;

    public OrderExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        log.warn("Pedido no encontrado en la API: {}", ex.getOrderId(), ex);
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

        log.warn("La petición contiene errores de validación: {}", errors, ex);

        return buildResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("El cuerpo de la petición no se pudo leer", ex);
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of(messageSource.getMessage(
                        "api.error.malformed-json",
                        null,
                        LocaleContextHolder.getLocale()
                ))
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                List.of(messageSource.getMessage(
                        "api.error.missing-parameter",
                        new Object[]{ex.getParameterName()},
                        LocaleContextHolder.getLocale()
                ))
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("Método HTTP no permitido: {}", ex.getMethod());
        var message = messageSource.getMessage(
                "api.error.method-not-allowed",
                null,
                LocaleContextHolder.getLocale()
        );
        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                List.of(message),
                ex.getHeaders()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Error interno no controlado en la API", ex);
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
        return buildResponse(status, errors, HttpHeaders.EMPTY);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            List<String> errors,
            HttpHeaders headers
    ) {
        var response = new ErrorResponse(
                LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                errors
        );

        return ResponseEntity.status(status).headers(headers).body(response);
    }

}
