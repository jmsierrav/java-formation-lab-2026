package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.geom.IllegalPathStateException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para InventoryPatchUseCase")
class InventoryPatchUseCaseTest {

    @Mock
    private IInventoryService service;

    @InjectMocks
    private InventoryPatchUseCase useCase;

    private ProductDto existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = ProductDto.builder()
                .id("product-1")
                .name("Laptop")
                .description("Original description")
                .units("unit")
                .quantity(1.0)
                .build();
    }

    @Test
    @DisplayName("Debe rechazar una actualizacion sin identificador")
    void update_withoutId_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.update(null, ProductDto.builder().build()));

        assertEquals("El registro no esta especificado para actualizar!", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe rechazar una actualizacion sin producto")
    void update_withoutProduct_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.update("product-1", null));

        assertEquals("El registro no esta especificado para actualizar!", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe rechazar la actualizacion de un producto inexistente")
    void update_withUnknownId_throwsIllegalPathStateException() {
        when(service.getById("missing")).thenReturn(List.of());

        IllegalPathStateException exception = assertThrows(IllegalPathStateException.class,
                () -> useCase.update("missing", ProductDto.builder().name("Laptop").build()));

        assertEquals("El registro no existe!", exception.getMessage());
        verify(service).getById("missing");
        verify(service, never()).update(any());
    }

    @Test
    @DisplayName("Debe combinar los campos proporcionados y conservar los restantes")
    void update_withPartialProduct_mergesProvidedFields() {
        ProductDto changes = ProductDto.builder()
                .name("Gaming laptop")
                .quantity(3.0)
                .build();
        when(service.getById("product-1")).thenReturn(List.of(existingProduct));

        useCase.update("product-1", changes);

        assertEquals("Gaming laptop", existingProduct.getName());
        assertEquals("Original description", existingProduct.getDescription());
        assertEquals("unit", existingProduct.getUnits());
        assertEquals(3.0, existingProduct.getQuantity());
        verify(service).update(existingProduct);
    }

    @ParameterizedTest
    @ValueSource(strings = {"description", "name", "units"})
    @DisplayName("Debe actualizar cada campo textual proporcionado")
    void update_withTextualField_mergesField(String field) {
        ProductDto changes = new ProductDto();
        String value = "updated-" + field;
        switch (field) {
            case "description" -> changes.setDescription(value);
            case "name" -> changes.setName(value);
            case "units" -> changes.setUnits(value);
            default -> throw new IllegalArgumentException("Campo no soportado");
        }
        when(service.getById("product-1")).thenReturn(List.of(existingProduct));

        useCase.update("product-1", changes);

        String actual = switch (field) {
            case "description" -> existingProduct.getDescription();
            case "name" -> existingProduct.getName();
            case "units" -> existingProduct.getUnits();
            default -> throw new IllegalArgumentException("Campo no soportado");
        };
        assertEquals(value, actual);
        verify(service).update(existingProduct);
    }

}
