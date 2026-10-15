package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.domain.InvalidSupplierException;
import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import com.indra.catalog.suppliers.domain.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    private SupplierRepository repository;

    private SupplierServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SupplierServiceImpl(repository);
    }

    @Test
    @DisplayName("Normaliza nombre y NIT antes de guardar el proveedor")
    void normalizesNameAndTaxIdBeforeSavingSupplier() {
        when(repository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Supplier created = service.create(new CreateSupplierCommand(
                "  acme ltda ", " 900.123.456-7 ", "compras@acme.co"));

        assertThat(created.id()).isEqualTo("SUP-001");
        assertThat(created.name()).isEqualTo("ACME LTDA");
        assertThat(created.taxId()).isEqualTo("900123456-7");
        assertThat(created.email()).isEqualTo("compras@acme.co");
        assertThat(created.internalNotes()).startsWith("Creado el ");
        verify(repository).save(created);
    }

    @Test
    @DisplayName("Asigna identificadores distintos a proveedores nuevos")
    void assignsDistinctIdsToNewSuppliers() {
        when(repository.save(any(Supplier.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateSupplierCommand command = new CreateSupplierCommand("ACME", "900123456-7", "compras@acme.co");

        Supplier first = service.create(command);
        Supplier second = service.create(command);

        assertThat(first.id()).isEqualTo("SUP-001");
        assertThat(second.id()).isEqualTo("SUP-002");
    }

    @ParameterizedTest
    @DisplayName("Rechaza nombres vacíos sin invocar el repositorio")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsMissingNameWithoutCallingRepository(String name) {
        assertThatThrownBy(() -> service.create(
                new CreateSupplierCommand(name, "900123456-7", "compras@acme.co")))
                .isInstanceOf(InvalidSupplierException.class)
                .hasMessageContaining("name");
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @DisplayName("Rechaza NIT inválidos después de normalizarlos sin invocar el repositorio")
    @NullAndEmptySource
    @ValueSource(strings = {" ", ".........-7"})
    void rejectsInvalidTaxIdAfterNormalizationWithoutCallingRepository(String taxId) {
        assertThatThrownBy(() -> service.create(
                new CreateSupplierCommand("ACME", taxId, "compras@acme.co")))
                .isInstanceOf(InvalidSupplierException.class)
                .hasMessageContaining("taxId");
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Devuelve el proveedor encontrado por el repositorio")
    void returnsSupplierFoundByRepository() {
        Supplier supplier = new Supplier("SUP-001", "ACME", "900123456-7", "compras@acme.co", null);
        when(repository.findById("SUP-001")).thenReturn(Optional.of(supplier));

        assertThat(service.findById("SUP-001")).isSameAs(supplier);
    }

    @Test
    @DisplayName("Rechaza la búsqueda cuando el proveedor no existe")
    void rejectsLookupWhenSupplierDoesNotExist() {
        when(repository.findById("SUP-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("SUP-999"))
                .isInstanceOf(SupplierNotFoundException.class);
    }

    @Test
    @DisplayName("Elimina un proveedor existente")
    void deletesExistingSupplier() {
        when(repository.existsById("SUP-001")).thenReturn(true);

        service.delete("SUP-001");

        verify(repository).deleteById("SUP-001");
    }

    @Test
    @DisplayName("Rechaza la eliminación cuando el proveedor no existe")
    void rejectsDeletionWhenSupplierDoesNotExist() {
        when(repository.existsById("SUP-999")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("SUP-999"))
                .isInstanceOf(SupplierNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
