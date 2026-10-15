package com.indra.catalog.products.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.domain.port.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    private ProductServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(repository);
    }

    @Test
    void createsProductWithGeneratedIdAndExactPrice() {
        when(repository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product created = service.create(new CreateProductCommand("Laptop", new BigDecimal("3500000.00"), 10));

        assertThat(created.id()).isEqualTo("PRD-0001");
        assertThat(created.price()).isEqualByComparingTo("3500000.00");
        verify(repository).save(created);
    }

    @Test
    void findsExistingProduct() {
        Product product = new Product("PRD-0001", "Laptop", BigDecimal.TEN, 10);
        when(repository.findById(product.id())).thenReturn(Optional.of(product));

        assertThat(service.findById(product.id())).isSameAs(product);
    }

    @Test
    void throwsWhenProductDoesNotExist() {
        when(repository.findById("PRD-9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("PRD-9999"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Producto no encontrado: PRD-9999");
    }

    @Test
    void deletesExistingProductAndRejectsMissingProduct() {
        when(repository.deleteById("PRD-0001")).thenReturn(true);
        service.delete("PRD-0001");
        verify(repository).deleteById("PRD-0001");

        when(repository.deleteById("PRD-9999")).thenReturn(false);
        assertThatThrownBy(() -> service.delete("PRD-9999"))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void rejectsInvalidProductBeforePersistence() {
        assertThatThrownBy(() -> service.create(
                new CreateProductCommand("Laptop", BigDecimal.ZERO, 1)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }
}
