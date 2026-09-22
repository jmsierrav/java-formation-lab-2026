package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias para InventoryGetUseCase")
class InventoryGetUseCaseTest {

    @Mock
    private IInventoryService service;

    @InjectMocks
    private InventoryGetUseCase useCase;

    @Test
    @DisplayName("Debe retornar una lista vacia cuando no recibe filtros")
    void read_withoutPathVariables_returnsEmptyList() {
        assertTrue(useCase.read(null).isEmpty());
        assertTrue(useCase.read(Map.of()).isEmpty());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Debe consultar por identificador cuando el filtro id esta presente")
    void read_withId_delegatesToGetById() {
        ProductDto product = ProductDto.builder().id("product-1").build();
        when(service.getById("product-1")).thenReturn(List.of(product));

        List<ProductDto> result = useCase.read(Map.of("id", "product-1"));

        assertEquals(List.of(product), result);
        verify(service).getById("product-1");
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("Debe priorizar el identificador cuando recibe id y name")
    void read_withIdAndName_delegatesOnlyToGetById() {
        when(service.getById("product-1")).thenReturn(List.of());

        useCase.read(Map.of("id", "product-1", "name", "Laptop"));

        verify(service).getById("product-1");
        verify(service, never()).getByName(anyString());
        verify(service, never()).getAll();
    }

    @Test
    @DisplayName("Debe consultar por nombre cuando el filtro name esta presente")
    void read_withName_delegatesToGetByName() {
        ProductDto product = ProductDto.builder().name("Laptop").build();
        when(service.getByName("Laptop")).thenReturn(List.of(product));

        List<ProductDto> result = useCase.read(Map.of("name", "Laptop"));

        assertEquals(List.of(product), result);
        verify(service).getByName("Laptop");
        verifyNoMoreInteractions(service);
    }

    @ParameterizedTest
    @CsvSource({"category,hardware", "status,available"})
    @DisplayName("Debe consultar todos los productos cuando recibe un filtro no soportado")
    void read_withUnsupportedFilter_delegatesToGetAll(String key, String value) {
        when(service.getAll()).thenReturn(List.of());

        List<ProductDto> result = useCase.read(Map.of(key, value));

        assertTrue(result.isEmpty());
        verify(service).getAll();
        verifyNoMoreInteractions(service);
    }

}
