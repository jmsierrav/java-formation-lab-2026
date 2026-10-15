package com.indra.catalog.suppliers.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.indra.catalog.suppliers.application.CreateSupplierCommand;
import com.indra.catalog.suppliers.application.SupplierService;
import com.indra.catalog.suppliers.domain.InvalidSupplierException;
import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierController.class)
class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierService supplierService;

    @Test
    @DisplayName("Devuelve el proveedor sin notas internas cuando existe")
    void returnsSupplierWithoutInternalNotesWhenFound() throws Exception {
        Supplier supplier = new Supplier("SUP-001", "ACME LTDA", "900123456-7",
                "compras@acme.co", "Datos internos");
        when(supplierService.findById("SUP-001")).thenReturn(supplier);

        mockMvc.perform(get("/api/suppliers/SUP-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.id").value("SUP-001"))
                .andExpect(jsonPath("$.name").value("ACME LTDA"))
                .andExpect(jsonPath("$.taxId").value("900123456-7"))
                .andExpect(jsonPath("$.email").value("compras@acme.co"))
                .andExpect(jsonPath("$.internalNotes").doesNotExist());
        verify(supplierService).findById("SUP-001");
    }

    @Test
    @DisplayName("Devuelve 404 cuando el proveedor no existe")
    void returnsNotFoundWhenSupplierDoesNotExist() throws Exception {
        when(supplierService.findById("SUP-999")).thenThrow(new SupplierNotFoundException("SUP-999"));

        mockMvc.perform(get("/api/suppliers/SUP-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SUPPLIER_NOT_FOUND"));
    }

    @Test
    @DisplayName("Delega la entrada sin modificar y devuelve el proveedor creado con su ubicación")
    void delegatesUnmodifiedInputAndReturnsCreatedSupplierWithLocation() throws Exception {
        CreateSupplierCommand command = new CreateSupplierCommand(
                "  acme ltda ", "900.123.456-7", "compras@acme.co");
        when(supplierService.create(command)).thenReturn(new Supplier(
                "SUP-001", "ACME LTDA", "900123456-7", "compras@acme.co", "Datos internos"));

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "  acme ltda ",
                                    "taxId": "900.123.456-7",
                                    "email": "compras@acme.co"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/suppliers/SUP-001"))
                .andExpect(jsonPath("$.id").value("SUP-001"))
                .andExpect(jsonPath("$.name").value("ACME LTDA"))
                .andExpect(jsonPath("$.taxId").value("900123456-7"))
                .andExpect(jsonPath("$.email").value("compras@acme.co"))
                .andExpect(jsonPath("$.internalNotes").doesNotExist());
        verify(supplierService).create(command);
    }

    @Test
    @DisplayName("Rechaza un nombre en blanco sin invocar el servicio")
    void rejectsBlankNameWithoutCallingService() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "taxId": "900.123.456-7",
                                    "email": "compras@acme.co"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").value(hasItem(containsString("name"))));
        verifyNoInteractions(supplierService);
    }

    @ParameterizedTest
    @DisplayName("Rechaza cuerpos ausentes o inválidos sin invocar el servicio")
    @ValueSource(strings = {"", " ", "null", "{", "{}"})
    void rejectsMissingOrInvalidBodyWithoutCallingService(String body) throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        verifyNoInteractions(supplierService);
    }

    @Test
    @DisplayName("Devuelve un error seguro cuando ocurre una excepción inesperada")
    void returnsSafeErrorWhenAnUnexpectedExceptionOccurs() throws Exception {
        when(supplierService.findById("SUP-001"))
                .thenThrow(new IllegalStateException("Database password=secret; host=internal-db"));

        mockMvc.perform(get("/api/suppliers/SUP-001"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.*", hasSize(5)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors").value(hasItem("Error interno del servidor")))
                .andExpect(jsonPath("$.path").value("/api/suppliers/SUP-001"));
    }

    @Test
    @DisplayName("Devuelve un error de validación cuando el dominio rechaza el proveedor")
    void returnsValidationErrorWhenDomainRejectsSupplier() throws Exception {
        CreateSupplierCommand command = new CreateSupplierCommand(
                "ACME LTDA", ".........-7", "compras@acme.co");
        when(supplierService.create(command))
                .thenThrow(new InvalidSupplierException("taxId: formato de NIT no valido"));

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"ACME LTDA","taxId":".........-7","email":"compras@acme.co"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").value(hasItem("taxId: formato de NIT no valido")));
        verify(supplierService).create(command);
    }

    @Test
    @DisplayName("Elimina el proveedor y devuelve 204")
    void deletesSupplierAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/suppliers/SUP-001"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
        verify(supplierService).delete("SUP-001");
    }
}
