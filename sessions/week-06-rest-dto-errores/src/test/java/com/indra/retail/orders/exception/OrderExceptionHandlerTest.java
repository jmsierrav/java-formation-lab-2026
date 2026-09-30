package com.indra.retail.orders.exception;

import com.indra.retail.orders.config.ApiLocaleConfiguration;
import com.indra.retail.orders.service.OrderService;
import com.indra.retail.orders.web.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({OrderExceptionHandler.class, ApiLocaleConfiguration.class})
class OrderExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderExceptionHandler exceptionHandler;

    @MockBean
    private OrderService orderService;

    @Test
    void returnsFieldErrorsForAnInvalidOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .locale(Locale.forLanguageTag("es"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "",
                                  "items": [],
                                  "deliveryAddress": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Identificador del cliente"))))
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Dirección de entrega"))));
    }

    @Test
    void returnsValidationMessagesInRequestedLanguage() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .locale(Locale.ENGLISH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-1",
                                  "items": [],
                                  "deliveryAddress": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("The order must contain at least one item."))))
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Delivery address"))));
    }

    @Test
    void returnsStandardErrorForMalformedJson() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .locale(Locale.forLanguageTag("es"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").value("El cuerpo JSON de la petición no es válido."));
    }

    @Test
    void returnsStandardErrorForMissingRequestParameter() {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("es"));
        try {
            var response = exceptionHandler.handleMissingParameter(
                    new MissingServletRequestParameterException("customerId", "String")
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().timestamp()).isNotNull();
            assertThat(response.getBody().status()).isEqualTo(400);
            assertThat(response.getBody().errors())
                    .containsExactly("Falta el parámetro requerido: customerId.");
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

    @Test
    void returnsStandardErrorAndAllowedMethodsForUnsupportedMethod() throws Exception {
        mockMvc.perform(post("/api/orders/missing")
                        .locale(Locale.forLanguageTag("es")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.errors[0]").value(
                        "El método HTTP solicitado no está permitido para esta ruta."))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                        .string("Allow", org.hamcrest.Matchers.containsString("GET")));
    }

    @Test
    void returnsLocalizedMessagesForInvalidOrderItems() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .locale(Locale.forLanguageTag("es"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-1",
                                  "items": [
                                    {"sku": "", "quantity": 0, "unitPrice": -1}
                                  ],
                                  "deliveryAddress": "A valid delivery address"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Código SKU: El código SKU es obligatorio."))))
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Cantidad: La cantidad debe ser al menos 1."))))
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Precio unitario: El precio unitario no puede ser negativo."))));
    }

    @Test
    void returnsDescriptiveMessageWhenOrderDoesNotExist() throws Exception {
        when(orderService.findById("missing")).thenThrow(new OrderNotFoundException("missing"));

        mockMvc.perform(get("/api/orders/missing")
                        .locale(Locale.forLanguageTag("es")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors[0]").value("Pedido no encontrado: missing"));
    }

    @Test
    void returnsGenericMessageForUnexpectedErrors() throws Exception {
        when(orderService.findById("broken")).thenThrow(new IllegalStateException("sensitive detail"));

        mockMvc.perform(get("/api/orders/broken")
                        .locale(Locale.forLanguageTag("es")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.errors[0]").value("Ocurrió un error interno. Inténtalo de nuevo más tarde."))
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensitive detail"))));
    }

    @Test
    void returnsNotFoundMessageInRequestedLanguage() throws Exception {
        when(orderService.findById("missing")).thenThrow(new OrderNotFoundException("missing"));

        mockMvc.perform(get("/api/orders/missing")
                        .locale(Locale.ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Order not found: missing"));
    }
}
