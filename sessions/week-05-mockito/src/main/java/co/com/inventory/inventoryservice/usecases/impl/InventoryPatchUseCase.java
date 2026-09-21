package co.com.inventory.inventoryservice.usecases.impl;

import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.services.IInventoryService;
import co.com.inventory.inventoryservice.usecases.IInventoryPatchUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.IllegalPathStateException;
import java.util.List;

@Service
public class InventoryPatchUseCase implements IInventoryPatchUseCase {

    private final IInventoryService service;

    @Autowired
    public InventoryPatchUseCase(IInventoryService service){
        this.service = service ;
    }

    @Override
    public void update(String id, ProductDto productDto) throws IllegalStateException, IllegalArgumentException {
        if(id == null || productDto == null)
            throw new IllegalArgumentException("El registro no esta especificado para actualizar!");

        List<ProductDto> producto = service.getById(id);

        if(producto.isEmpty()){
            throw new IllegalPathStateException("El registro no existe!");
        }

        if(productDto.getDescription() != null){
            producto.getFirst().setDescription(productDto.getDescription());
        }

        if(productDto.getName() != null){
            producto.getFirst().setName(productDto.getName());
        }

        if(productDto.getUnits() != null){
            producto.getFirst().setUnits(productDto.getUnits());
        }

        if(productDto.getQuantity() != null){
            producto.getFirst().setQuantity(productDto.getQuantity());
        }

        service.update(producto.getFirst());
    }

}
