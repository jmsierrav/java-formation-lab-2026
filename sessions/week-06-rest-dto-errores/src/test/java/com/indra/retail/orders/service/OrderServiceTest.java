package com.indra.retail.orders.service;

import com.indra.retail.orders.dto.CreateOrderRequest;
import com.indra.retail.orders.dto.Item;
import com.indra.retail.orders.dto.OrderResponse;
import com.indra.retail.orders.exception.OrderNotFoundException;
import com.indra.retail.orders.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderService orderService = new OrderService();

    @Mock
    private CreateOrderRequest request;

    @Test
    @DisplayName("Crea un pedido con identificador, estado y fecha de entrega")
    void createsOrderWithInitialStateAndDeliveryDate() {
        when(request.customerId()).thenReturn("customer-1");
        when(request.items()).thenReturn(List.of(new Item("SKU-1", 2, 12.5)));
        when(request.deliveryAddress()).thenReturn("123 Main Street");
        LocalDate todayBeforeCreation = LocalDate.now();

        OrderResponse response = orderService.create(request);

        assertThat(UUID.fromString(response.orderId()).toString()).hasToString(response.orderId());
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.totalAmount()).isEqualTo(25.0);
        assertThat(response.estimatedDelivery())
                .isBetween(todayBeforeCreation.plusDays(5), LocalDate.now().plusDays(5));
    }

    @ParameterizedTest(name = "({0} x {1}) + ({2} x {3}) = {4}")
    @CsvSource({
            "2, 12.5, 1, 5.0, 30.0",
            "1, 0.0, 3, 10.0, 30.0",
            "3, 0.1, 2, 0.2, 0.7"
    })
    @DisplayName("Calcula el total a partir de las cantidades y precios de los artículos")
    void calculatesTotalForMultipleItems(int firstQuantity, double firstPrice,
                                         int secondQuantity, double secondPrice, double expectedTotal) {
        var createOrderRequest = new CreateOrderRequest("customer-1", List.of(
                new Item("SKU-1", firstQuantity, firstPrice),
                new Item("SKU-2", secondQuantity, secondPrice)
        ), "123 Main Street");

        OrderResponse response = orderService.create(createOrderRequest);

        assertThat(response.totalAmount()).isCloseTo(expectedTotal, offset(1e-9));
    }

    @Test
    @DisplayName("Recupera cada pedido por su identificador sin mezclar sus datos")
    void findsCreatedOrdersById() {
        OrderResponse first = orderService.create(new CreateOrderRequest(
                "customer-1", List.of(new Item("SKU-1", 2, 12.5)), "123 Main Street"));
        OrderResponse second = orderService.create(new CreateOrderRequest(
                "customer-2", List.of(new Item("SKU-2", 1, 7.0)), "456 Main Street"));

        assertThat(first.orderId()).isNotEqualTo(second.orderId());
        assertThat(orderService.findById(first.orderId())).isEqualTo(first);
        assertThat(orderService.findById(second.orderId())).isEqualTo(second);
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "00000000-0000-0000-0000-000000000000"})
    @DisplayName("Lanza OrderNotFoundException con el identificador solicitado si no existe")
    void rejectsUnknownOrderId(String orderId) {
        assertThatExceptionOfType(OrderNotFoundException.class)
                .isThrownBy(() -> orderService.findById(orderId))
                .satisfies(error -> assertThat(error.getOrderId()).isEqualTo(orderId));
    }
}
