package com.indra.retail.manager;

import com.indra.retail.entities.Address;
import com.indra.retail.entities.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CustomerAddressTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("Persiste y recupera todos los campos de la direccion embebida")
    void shouldPersistAndLoadAddress() {
        Customer saved = createCustomerWithAddress();

        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();

        assertAddress(found.getAddress(), "Carrera", "7", "45-10", "Chapinero", "Bogota");
    }

    @Test
    @DisplayName("Conserva la compatibilidad con clientes sin direccion")
    void shouldPersistCustomerWithoutAddress() {
        Customer saved = manager.create(new Customer("Ana", uniqueEmail()));

        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();

        assertNull(found.getAddress());
    }

    @Test
    @DisplayName("Detecta cambios en los campos de una direccion dentro de la transaccion")
    void shouldUpdateManagedAddressFields() {
        Customer saved = createCustomerWithAddress();

        transactionTemplate.executeWithoutResult(status -> {
            Customer managed = manager.findById(Customer.class, saved.getId()).orElseThrow();
            managed.getAddress().setCity("Medellin");
            managed.getAddress().setNeighborhood("Laureles");
        });

        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();
        assertAddress(found.getAddress(), "Carrera", "7", "45-10", "Laureles", "Medellin");
    }

    @Test
    @DisplayName("Reemplaza la direccion de un cliente detached mediante merge")
    void shouldReplaceAddressOnMerge() {
        Customer saved = createCustomerWithAddress();
        Customer detached = manager.findById(Customer.class, saved.getId()).orElseThrow();
        detached.setAddress(address("Calle", "10", "20-30", "San Fernando", "Cali"));

        manager.update(detached);

        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();
        assertAddress(found.getAddress(), "Calle", "10", "20-30", "San Fernando", "Cali");
    }

    @Test
    @DisplayName("Eliminar la direccion no elimina al cliente")
    void shouldRemoveAddressWithoutDeletingCustomer() {
        Customer saved = createCustomerWithAddress();
        Customer detached = manager.findById(Customer.class, saved.getId()).orElseThrow();
        detached.setAddress(null);

        manager.update(detached);

        Customer found = manager.findById(Customer.class, saved.getId()).orElseThrow();
        assertNull(found.getAddress());
        assertEquals("Ana", found.getName());
        assertEquals(saved.getEmail(), found.getEmail());
    }

    private Customer createCustomerWithAddress() {
        Customer customer = new Customer("Ana", uniqueEmail());
        customer.setAddress(address("Carrera", "7", "45-10", "Chapinero", "Bogota"));
        return manager.create(customer);
    }

    private static String uniqueEmail() {
        return UUID.randomUUID() + "@indracompany.com";
    }

    private static Address address(String streetType, String streetNumber, String number,
                                   String neighborhood, String city) {
        Address address = new Address();
        address.setStreetType(streetType);
        address.setStreetNumber(streetNumber);
        address.setNumber(number);
        address.setNeighborhood(neighborhood);
        address.setCity(city);
        return address;
    }

    private static void assertAddress(Address address, String streetType, String streetNumber,
                                      String number, String neighborhood, String city) {
        assertNotNull(address);
        assertEquals(streetType, address.getStreetType());
        assertEquals(streetNumber, address.getStreetNumber());
        assertEquals(number, address.getNumber());
        assertEquals(neighborhood, address.getNeighborhood());
        assertEquals(city, address.getCity());
    }
}
