package com.indra.catalog.products.domain.exception;

public class InvalidProductException extends IllegalArgumentException {

    public InvalidProductException(String message) {
        super(message);
    }
}
