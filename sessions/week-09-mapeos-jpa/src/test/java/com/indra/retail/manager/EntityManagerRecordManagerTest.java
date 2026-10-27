package com.indra.retail.manager;

import com.indra.retail.entities.Customer;
import com.indra.retail.entities.Order;
import com.indra.retail.entities.OrderItem;
import com.indra.retail.entities.Product;
import com.indra.retail.valueobjects.OrderStatus;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CRUD básico (crear, consultar, actualizar, eliminar) con EntityManager.
 * Cada test usa datos únicos, así que no dependen del orden de ejecución.
 */
@SpringBootTest
class EntityManagerRecordManagerTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@indracompany.com";
    }

    @Test
    @DisplayName("Crea un Customer y lo consulta por id")
    void shouldCreateAndFindCustomer() {
        Customer saved = manager.create(new Customer("Ana", uniqueEmail()));

        assertNotNull(saved.getId());
        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();
        assertEquals("Ana", found.getName());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    @DisplayName("Rechaza clientes con email duplicado")
    void shouldRejectDuplicateCustomerEmail() {
        String email = uniqueEmail();
        manager.create(new Customer("Ana", email));

        assertThrows(DataIntegrityViolationException.class,
                () -> manager.create(new Customer("Luis", email)));
    }

    @Test
    @DisplayName("Actualiza un Product")
    void shouldUpdateProduct() {
        Product saved = manager.create(new Product("Teclado", new BigDecimal("50.00"), 10));

        saved.setStock(7);
        manager.update(saved);

        Product found = manager.findById(Product.class, saved.getId()).orElseThrow();
        assertEquals(7, found.getStock());
    }

    @Test
    @DisplayName("Elimina un Customer")
    void shouldDeleteCustomer() {
        Customer saved = manager.create(new Customer("Luis", uniqueEmail()));

        assertTrue(manager.delete(Customer.class, saved.getId()));

        assertFalse(manager.findById(Customer.class, saved.getId()).isPresent());
    }

    @Test
    @DisplayName("Devuelve false al eliminar un registro inexistente")
    void shouldReturnFalseWhenDeletingMissingRecord() {
        assertFalse(manager.delete(Customer.class, Long.MAX_VALUE));
    }

    @Test
    @DisplayName("Lista los Product creados")
    void shouldListProducts() {
        Product saved = manager.create(new Product("Mouse", new BigDecimal("20.00"), 5));

        assertTrue(manager.findAll(Product.class).stream()
                .anyMatch(p -> p.getId().equals(saved.getId())));
    }

    @Test
    @DisplayName("Persiste un Order con sus ítems en cascada")
    void shouldPersistOrderWithItems() {
        Long[] ids = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Marta", uniqueEmail()));
            Product product = manager.create(new Product("Monitor", new BigDecimal("300.00"), 3));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 2, product.getPrice()));
            manager.create(order);
            return new Long[] {order.getId(), customer.getId()};
        });

        Order found = manager.findById(Order.class, ids[0]).orElseThrow();
        assertEquals(OrderStatus.PENDING, found.getStatus());
        assertNotNull(found.getCreatedAt());
        assertEquals(ids[1], found.getCustomer().getId());
    }

    @Test
    @DisplayName("Actualiza el estado y los items de una orden detached mediante MERGE")
    void shouldMergeOrderWithItems() {
        Long orderId = transactionTemplate.execute(status -> {
            Customer customer = manager.create(new Customer("Marta", uniqueEmail()));
            Product product = manager.create(new Product("Monitor", new BigDecimal("300.00"), 3));
            Order order = new Order(customer);
            order.addItem(new OrderItem(product, 1, product.getPrice()));
            return manager.create(order).getId();
        });
        Order detached = manager.findOrderWithItems(orderId).orElseThrow();
        detached.setStatus(OrderStatus.CONFIRMED);
        detached.getItems().getFirst().setQuantity(2);

        manager.update(detached);

        Order found = manager.findOrderWithItems(orderId).orElseThrow();
        assertEquals(OrderStatus.CONFIRMED, found.getStatus());
        assertEquals(2, found.getItems().getFirst().getQuantity());
    }
}
