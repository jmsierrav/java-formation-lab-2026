package com.indra.catalog.products;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "catalog.security.editor-password=test-editor-password",
        "catalog.security.admin-password=test-admin-password"
})
@AutoConfigureMockMvc
class ProductCatalogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Integra la API de productos con seguridad y todos sus adaptadores")
    void wiresProductApiAndSecurityAcrossAllAdapters() throws Exception {
        mockMvc.perform(post("/api/products")
                        .with(httpBasic("editor", "test-editor-password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Laptop","price":3500000.00,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/PRD-0001"));

        mockMvc.perform(get("/api/products/PRD-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        mockMvc.perform(delete("/api/products/PRD-0001")
                        .with(httpBasic("admin", "test-admin-password")))
                .andExpect(status().isNoContent());
    }
}
