package com.indra.retail.manager;

import com.indra.retail.entities.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductVersionTest {

    @Autowired
    private EntityManagerRecordManager manager;

    @Test
    @DisplayName("Hibernate inicializa y persiste la version de un producto nuevo")
    void shouldInitializeVersionOnPersist() {
        Product product = new Product("Google Pixel 11", new BigDecimal("999.00"), 10);
        assertNull(product.getVersion());

        Product saved = manager.create(product);
        Product found = manager.findById(Product.class, saved.getId()).orElseThrow();

        assertNotNull(saved.getVersion());
        assertEquals(saved.getVersion(), found.getVersion());
    }

    @Test
    @DisplayName("Actualizar un producto incrementa su version en la instancia retornada por merge")
    void shouldIncrementVersionOnUpdate() {
        Product saved = manager.create(new Product("Google Pixel 11", new BigDecimal("999.00"), 10));
        Long originalVersion = saved.getVersion();
        saved.setStock(7);

        Product updated = manager.update(saved);
        Product found = manager.findById(Product.class, saved.getId()).orElseThrow();

        assertEquals(originalVersion + 1, updated.getVersion());
        assertEquals(updated.getVersion(), found.getVersion());
        assertEquals(7, found.getStock());
        assertEquals(originalVersion, saved.getVersion());
    }

    @Test
    @DisplayName("Una copia obsoleta no sobrescribe los cambios confirmados por otra transaccion")
    void shouldRejectStaleUpdateWithoutOverwritingCommittedChanges() {
        Product saved = manager.create(new Product("Google Pixel 11", new BigDecimal("999.00"), 10));
        Product firstCopy = manager.findById(Product.class, saved.getId()).orElseThrow();
        Product staleCopy = manager.findById(Product.class, saved.getId()).orElseThrow();
        assertEquals(firstCopy.getVersion(), staleCopy.getVersion());

        firstCopy.setStock(7);
        Product updated = manager.update(firstCopy);
        staleCopy.setStock(3);

        assertThrows(OptimisticLockingFailureException.class, () -> manager.update(staleCopy));

        Product found = manager.findById(Product.class, saved.getId()).orElseThrow();
        assertEquals(7, found.getStock());
        assertEquals(updated.getVersion(), found.getVersion());
    }
}
