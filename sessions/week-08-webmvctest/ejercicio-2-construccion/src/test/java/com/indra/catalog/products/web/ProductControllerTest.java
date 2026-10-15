package com.indra.catalog.products.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.indra.catalog.products.application.CreateProductCommand;
import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.infrastructure.security.SecurityConfiguration;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ProductController.class, properties = {
        "catalog.security.editor-password=test-editor-password",
        "catalog.security.admin-password=test-admin-password"
})
@Import(SecurityConfiguration.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Devuelve un producto como JSON cuando existe")
    void returnsProductAsJsonWhenItExists() throws Exception {
        when(productService.findById("PRD-0001"))
                .thenReturn(new Product("PRD-0001", "Laptop", new BigDecimal("3500000.00"), 10));

        mockMvc.perform(get("/api/products/PRD-0001"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("PRD-0001"))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(3500000.00))
                .andExpect(jsonPath("$.stock").value(10));
        verify(productService).findById("PRD-0001");
    }

    @Test
    @DisplayName("Devuelve 404 cuando el producto no existe")
    void returnsNotFoundForUnknownProduct() throws Exception {
        when(productService.findById("PRD-9999")).thenThrow(new ProductNotFoundException("PRD-9999"));

        mockMvc.perform(get("/api/products/PRD-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
                .andExpect(jsonPath("$.errors[0]").value("Producto no encontrado: PRD-9999"))
                .andExpect(jsonPath("$.path").value("/api/products/PRD-9999"));
    }

    @Test
    @DisplayName("Crea un producto para un editor y devuelve su ubicación")
    void createsProductAndReturnsLocationForEditor() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "Laptop", new BigDecimal("3500000.00"), 10);
        when(productService.create(command))
                .thenReturn(new Product("PRD-0001", "Laptop", command.price(), command.stock()));

        mockMvc.perform(post("/api/products")
                        .with(httpBasic("editor", "test-editor-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laptop","price":3500000.00,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/PRD-0001"))
                .andExpect(jsonPath("$.id").value("PRD-0001"))
                .andExpect(jsonPath("$.price").value(3500000.00));
        verify(productService).create(command);
    }

    @Test
    @DisplayName("Devuelve un error de validación cuando el nombre está vacío")
    void returnsValidationErrorForBlankName() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(httpBasic("editor", "test-editor-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":" ","price":3500000.00,"stock":10}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors[0]").value(containsString("name")));
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Exige autenticación para crear un producto")
    void requiresAuthenticationToCreate() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laptop","price":3500000.00,"stock":10}
                                """))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Exige el rol administrador para eliminar un producto")
    void requiresAdminRoleToDelete() throws Exception {
        mockMvc.perform(delete("/api/products/PRD-0001")
                        .with(httpBasic("editor", "test-editor-password")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Elimina un producto como administrador y devuelve 204")
    void deletesProductAsAdminAndReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/PRD-0001")
                        .with(httpBasic("admin", "test-admin-password")))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(productService).delete("PRD-0001");
    }
}
