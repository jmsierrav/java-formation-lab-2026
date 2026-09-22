package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para InventoryPostUseCase")
class InventoryPostUseCaseTest {

    @Mock
    private IInventoryService service;

    @InjectMocks
    private InventoryPostUseCase useCase;

    @Test
    @DisplayName("Debe rechazar un producto nulo")
    void create_withNullProduct_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(null));

        assertEquals("La información del producto esta vacía!", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe rechazar un producto sin nombre")
    void create_withoutName_throwsIllegalArgumentException() {
        ProductDto product = ProductDto.builder().build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(product));

        assertEquals("El nombre del producto se encuentra vacío!", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe rechazar un identificador que ya existe")
    void create_withExistingId_throwsIllegalStateException() {
        ProductDto product = ProductDto.builder().id("product-1").name("Laptop").build();
        when(service.getById("product-1")).thenReturn(List.of(product));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.create(product));

        assertEquals("El identificador del producto ya existe!", exception.getMessage());
        verify(service).getById("product-1");
        verify(service, never()).getByName(anyString());
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Debe rechazar un nombre que ya existe")
    void create_withExistingName_throwsIllegalStateException() {
        ProductDto product = ProductDto.builder().name("Laptop").build();
        when(service.getByName("Laptop")).thenReturn(List.of(product));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.create(product));

        assertEquals("Ya existe un producto con ese nombre!", exception.getMessage());
        verify(service).getByName("Laptop");
        verify(service, never()).create(any());
    }

    @Test
    @DisplayName("Debe crear un producto cuando el identificador provisto esta disponible")
    void create_withAvailableId_delegatesCreation() {
        ProductDto product = ProductDto.builder().id("product-1").name("Laptop").build();
        when(service.getById("product-1")).thenReturn(List.of());
        when(service.getByName("Laptop")).thenReturn(List.of());
        when(service.create(product)).thenReturn("product-1");

        String result = useCase.create(product);

        assertEquals("product-1", result);
        verify(service).getById("product-1");
        verify(service).getByName("Laptop");
        verify(service).create(product);
    }

    @Test
    @DisplayName("Debe asignar un identificador y crear un producto valido")
    void create_withAvailableProduct_assignsIdAndDelegatesCreation() {
        ProductDto product = ProductDto.builder().name("Laptop").build();
        when(service.getByName("Laptop")).thenReturn(List.of());
        when(service.create(any(ProductDto.class))).thenReturn("created-id");
        ArgumentCaptor<ProductDto> productCaptor = ArgumentCaptor.forClass(ProductDto.class);

        String result = useCase.create(product);

        assertEquals("created-id", result);
        verify(service).create(productCaptor.capture());
        assertSame(product, productCaptor.getValue());
        assertNotNull(productCaptor.getValue().getId());
        verify(service).getByName("Laptop");
    }

}
