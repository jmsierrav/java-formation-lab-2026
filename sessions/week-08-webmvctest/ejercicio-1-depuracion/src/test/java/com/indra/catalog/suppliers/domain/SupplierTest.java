package com.indra.catalog.suppliers.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SupplierTest {

    @Test
    void createsSupplierWithRequiredFieldsAndOptionalNotes() {
        Supplier supplier = new Supplier("SUP-001", "ACME", "900123456-7", "compras@acme.co", null);

        assertThat(supplier.id()).isEqualTo("SUP-001");
        assertThat(supplier.name()).isEqualTo("ACME");
        assertThat(supplier.taxId()).isEqualTo("900123456-7");
        assertThat(supplier.email()).isEqualTo("compras@acme.co");
        assertThat(supplier.internalNotes()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingRequiredFields(String value) {
        assertThatThrownBy(() -> new Supplier(value, "ACME", "900123456-7", "compras@acme.co", null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("id");
        assertThatThrownBy(() -> new Supplier("SUP-001", value, "900123456-7", "compras@acme.co", null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("name");
        assertThatThrownBy(() -> new Supplier("SUP-001", "ACME", value, "compras@acme.co", null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("taxId");
        assertThatThrownBy(() -> new Supplier("SUP-001", "ACME", "900123456-7", value, null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("email");
    }

    @ParameterizedTest
    @ValueSource(strings = {"900.123.456-7", ".........-7", "123-7", "9001234567"})
    void rejectsTaxIdOutsideCanonicalFormat(String taxId) {
        assertThatThrownBy(() -> new Supplier("SUP-001", "ACME", taxId, "compras@acme.co", null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("taxId");
    }

    @ParameterizedTest
    @ValueSource(strings = {"compras", "@acme.co", "compras@", "compras@@acme.co", "compras @acme.co"})
    void rejectsMalformedEmail(String email) {
        assertThatThrownBy(() -> new Supplier("SUP-001", "ACME", "900123456-7", email, null))
                .isInstanceOf(InvalidSupplierException.class).hasMessageContaining("email");
    }
}
