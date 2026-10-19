package com.indra.catalog.suppliers.domain;

public class SupplierNotFoundException extends RuntimeException {

    public SupplierNotFoundException(String supplierId) {
        super("Proveedor no encontrado: " + supplierId);
    }
}
