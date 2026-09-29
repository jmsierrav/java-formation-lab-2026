package com.indra.retail.orders.exception;

import com.indra.retail.orders.service.OrderService;
import com.indra.retail.orders.web.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(OrderExceptionHandler.class)
class OrderExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void returnsFieldErrorsForAnInvalidOrder() throws Exception {
        mockMvc.perform(post("/api/orders")
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
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void returnsDescriptiveMessageWhenOrderDoesNotExist() throws Exception {
        when(orderService.findById("missing")).thenThrow(new OrderNotFoundException("missing"));

        mockMvc.perform(get("/api/orders/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errors[0]").value("Pedido no encontrado: missing"));
    }

    @Test
    void returnsGenericMessageForUnexpectedErrors() throws Exception {
        when(orderService.findById("broken")).thenThrow(new IllegalStateException("sensitive detail"));

        mockMvc.perform(get("/api/orders/broken"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.errors[0]").value("Ocurrió un error interno. Inténtalo de nuevo más tarde."))
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensitive detail"))));
    }
}
