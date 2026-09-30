package com.indra.retail.orders.web;

import com.indra.retail.orders.config.ApiLocaleConfiguration;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.exception.OrderNotFoundException;
import com.indra.retail.orders.exception.OrderExceptionHandler;
import com.indra.retail.orders.model.OrderStatus;
import com.indra.retail.orders.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({OrderExceptionHandler.class, ApiLocaleConfiguration.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Crea un pedido y devuelve la respuesta con estado 201")
    void createsOrderAndReturnsCreatedResponse() throws Exception {
        when(orderService.create(any())).thenReturn(new OrderResponse(
                "order-123",
                OrderStatus.CREATED,
                25.0,
                LocalDate.of(2026, 10, 7)
        ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "customer-1",
                                  "items": [
                                    {"sku": "SKU-1", "quantity": 2, "unitPrice": 12.5}
                                  ],
                                  "deliveryAddress": "123 Main Street"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(25.0))
                .andExpect(jsonPath("$.estimatedDelivery").value("2026-10-07"));

        verify(orderService).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    @DisplayName("Rechaza un identificador de cliente vacío o en blanco")
    void rejectsInvalidOrderAndReturnsBadRequest(String customerId) throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": "%s",
                                  "items": [
                                    {"sku": "SKU-1", "quantity": 1, "unitPrice": 10.0}
                                  ],
                                  "deliveryAddress": "123 Main Street"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.containsString("Identificador del cliente"))));

        verify(orderService, never()).create(any());
    }

    @Test
    @DisplayName("Recupera un pedido y devuelve la respuesta con estado 200")
    void getsOrderAndReturnsOkResponse() throws Exception {
        when(orderService.findById("order-123")).thenReturn(new OrderResponse(
                "order-123",
                OrderStatus.CREATED,
                25.0,
                LocalDate.of(2026, 10, 7)
        ));

        mockMvc.perform(get("/api/orders/order-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(25.0))
                .andExpect(jsonPath("$.estimatedDelivery").value("2026-10-07"));

        verify(orderService).findById("order-123");
    }

    @Test
    @DisplayName("Devuelve 404 cuando el pedido solicitado no existe")
    void returnsNotFoundWhenOrderDoesNotExist() throws Exception {
        when(orderService.findById("order-123"))
                .thenThrow(new OrderNotFoundException("order-123"));

        mockMvc.perform(get("/api/orders/order-123")
                        .locale(Locale.forLanguageTag("es")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors[0]").value("Pedido no encontrado: order-123"));

        verify(orderService).findById("order-123");
    }
}
