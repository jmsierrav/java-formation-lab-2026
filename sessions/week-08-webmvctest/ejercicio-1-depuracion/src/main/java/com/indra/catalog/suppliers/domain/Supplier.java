package com.indra.catalog.suppliers.domain;

public record Supplier(String id, String name, String taxId, String email, String internalNotes) {

    public Supplier {
        requireNotBlank(id, "id");
        requireNotBlank(name, "name");
        requireNotBlank(taxId, "taxId");
        requireNotBlank(email, "email");
        if (!taxId.matches("[0-9]{9,11}-[0-9]")) {
            throw new InvalidSupplierException("taxId: formato de NIT no valido");
        }
        if (!email.matches("[^\\s@]+@[^\\s@]+")) {
            throw new InvalidSupplierException("email: formato de correo no valido");
        }
    }

    private static void requireNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidSupplierException(field + ": es obligatorio");
        }
    }

}
