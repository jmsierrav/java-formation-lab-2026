package com.indra.catalog.suppliers.domain;

public class InvalidSupplierException extends IllegalArgumentException {

    public InvalidSupplierException(String message) {
        super(message);
    }
}
