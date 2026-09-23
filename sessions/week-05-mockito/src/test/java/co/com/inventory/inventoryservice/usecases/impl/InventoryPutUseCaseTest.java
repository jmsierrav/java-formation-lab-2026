package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.geom.IllegalPathStateException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para InventoryPutUseCase")
class InventoryPutUseCaseTest {

    @Mock
    private IInventoryService service;

    @InjectMocks
    private InventoryPutUseCase useCase;

    @Test
    @DisplayName("Debe rechazar un producto nulo")
    void update_withNullProduct_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.update(null));

        assertEquals("El registro no esta especificado para actualizar!", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe rechazar la actualizacion de un producto inexistente")
    void update_withUnknownId_throwsIllegalPathStateException() {
        ProductDto product = ProductDto.builder().id("missing").build();
        when(service.getById("missing")).thenReturn(List.of());

        IllegalPathStateException exception = assertThrows(IllegalPathStateException.class,
                () -> useCase.update(product));

        assertEquals("El registro no existe!", exception.getMessage());
        verify(service).getById("missing");
        verify(service, never()).update(any());
    }

    @Test
    @DisplayName("Debe actualizar un producto existente")
    void update_withExistingProduct_delegatesUpdate() {
        ProductDto product = ProductDto.builder().id("product-1").name("Laptop").build();
        when(service.getById("product-1")).thenReturn(List.of(product));

        useCase.update(product);

        verify(service).getById("product-1");
        verify(service).update(product);
    }

}
