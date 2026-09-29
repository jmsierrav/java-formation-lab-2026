package com.indra.retail.orders.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("Pedido no encontrado: " + orderId);
    }

}
