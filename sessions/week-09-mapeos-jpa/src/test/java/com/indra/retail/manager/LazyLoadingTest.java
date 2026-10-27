package com.indra.retail.manager;

import com.indra.retail.entities.Customer;
import com.indra.retail.entities.Order;
import com.indra.retail.entities.OrderItem;
import com.indra.retail.entities.Product;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LazyLoadingTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long orderId;

    @BeforeEach
    void createOrderWithItems() {
        orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Carla", UUID.randomUUID() + "@indra.com"));
            Product product = manager.create(new Product("Laptop", new BigDecimal("1500.00"), 4));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });
    }

    @Test
    @DisplayName("Acceder a items fuera de la transacción lanza LazyInitializationException")
    void shouldThrowLazyInitializationExceptionOutsideTransaction() {
        // findById abre y cierra su transacción: la Order sale DETACHED
        Order order = manager.findById(Order.class, orderId).orElseThrow();

        assertThrows(LazyInitializationException.class, () -> order.getItems().size());
    }

    @Test
    @DisplayName("Acceder a items dentro de una transacción activa funciona")
    void shouldLoadItemsInsideTransaction() {
        int itemCount = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getItems().size();
        });

        assertEquals(1, itemCount);
    }

    @Test
    @DisplayName("JOIN FETCH carga todos los items incluso fuera de la transaccion")
    void shouldLoadOrderWithItemsOutsideTransaction() {
        transactionTemplate.executeWithoutResult(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            Product product = manager.create(new Product("Mouse", new BigDecimal("20.00"), 5));
            order.addItem(new OrderItem(product, 2, product.getPrice()));
            manager.update(order);
        });

        Order order = manager.findOrderWithItems(orderId).orElseThrow();

        assertTrue(Hibernate.isInitialized(order.getItems()));
        assertEquals(2, order.getItems().size());
        assertEquals(3, order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
        assertTrue(order.getItems().stream().allMatch(item -> item.getUnitPrice() != null));
        assertFalse(Hibernate.isInitialized(order.getCustomer()));
        assertTrue(order.getItems().stream().noneMatch(item -> Hibernate.isInitialized(item.getProduct())));
    }

    @Test
    @DisplayName("JOIN FETCH conserva ordenes sin items")
    void shouldLoadOrderWithoutItems() {
        Long emptyOrderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Ana", UUID.randomUUID() + "@indra.com"));
            return manager.create(new Order(customer)).getId();
        });

        Order order = manager.findOrderWithItems(emptyOrderId).orElseThrow();

        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenOrderDoesNotExist() {
        assertTrue(manager.findOrderWithItems(Long.MAX_VALUE).isEmpty());
    }

    @Test
    @DisplayName("Customer de un Order no se carga hasta que se usa (LAZY)")
    void shouldLoadCustomerLazily() {
        Order detached = manager.findById(Order.class, orderId).orElseThrow();

        assertThrows(LazyInitializationException.class, () -> detached.getCustomer().getName());

        String customerName = transactionTemplate.execute(status ->
                manager.findById(Order.class, orderId).orElseThrow().getCustomer().getName());
        assertEquals("Carla", customerName);
    }

    @Test
    @DisplayName("Quitar un ítem de la lista elimina la fila (orphanRemoval)")
    void shouldRemoveOrphanItem() {
        Long itemId = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            return order.getItems().removeFirst().getId();
        });

        assertTrue(manager.findById(OrderItem.class, itemId).isEmpty());
        assertTrue(manager.findOrderWithItems(orderId).orElseThrow().getItems().isEmpty());
    }

    @Test
    @DisplayName("No se puede guardar un OrderItem sin Order")
    void shouldRejectOrphanOrderItem() {
        Product product = manager.create(new Product("Monitor", new BigDecimal("300.00"), 3));

        assertThrows(DataIntegrityViolationException.class,
                () -> manager.create(new OrderItem(product, 1, product.getPrice())));
    }

    @Test
    @DisplayName("Borrar un OrderItem no borra el Product")
    void shouldNotDeleteProductWhenDeletingItem() {
        Long[] ids = transactionTemplate.execute(status -> {
            OrderItem item = manager.findById(Order.class, orderId).orElseThrow().getItems().getFirst();
            return new Long[] {item.getId(), item.getProduct().getId()};
        });

        assertTrue(manager.delete(OrderItem.class, ids[0]));

        assertTrue(manager.findById(Product.class, ids[1]).isPresent());
        assertTrue(manager.findById(Order.class, orderId).isPresent());
    }

    @Test
    @DisplayName("Borrar una orden elimina sus items pero conserva cliente y producto")
    void shouldDeleteOrderWithoutDeletingCustomerOrProduct() {
        Long[] ids = transactionTemplate.execute(status -> {
            Order order = manager.findById(Order.class, orderId).orElseThrow();
            OrderItem item = order.getItems().getFirst();
            return new Long[] {item.getId(), item.getProduct().getId(), order.getCustomer().getId()};
        });

        assertTrue(manager.delete(Order.class, orderId));

        assertTrue(manager.findById(OrderItem.class, ids[0]).isEmpty());
        assertTrue(manager.findById(Product.class, ids[1]).isPresent());
        assertTrue(manager.findById(Customer.class, ids[2]).isPresent());
    }
}
