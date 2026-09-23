package co.com.inventory.inventoryservice.services.impl;

import co.com.inventory.inventoryservice.entities.Product;
import co.com.inventory.inventoryservice.models.ProductDto;
import co.com.inventory.inventoryservice.repositories.ICatalogRepository;
import co.com.inventory.inventoryservice.services.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InventoryService implements IInventoryService {

    private final ICatalogRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public InventoryService(ICatalogRepository repository) {
        this.repository = repository;
        modelMapper = new ModelMapper();
    }

    @Override
    public String create(ProductDto productDto) throws IllegalStateException {
        try {
            Product product = modelMapper.map(productDto, Product.class);
            product = repository.save(product);

            return product.getId();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw  new IllegalStateException("Proceso de creación NO exitoso!");
        }
    }

    @Override
    public void update(ProductDto productDto) throws IllegalStateException {
        try {
            Product product = modelMapper.map(productDto, Product.class);
            repository.save(product);
        } catch (Exception e) {
            throw  new IllegalStateException("Proceso de actualización NO exitoso!");
        }
    }

    @Override
    public List<ProductDto> getById(String id) {
        Optional<Product> productOptional = repository.findById(id);
        List<ProductDto> values = new ArrayList<>();
        productOptional.ifPresent(product -> values.add(modelMapper.map(product, ProductDto.class)));

        return values;
    }

    @Override
    public List<ProductDto> getByName(String name) {
        List<Product> results = repository.findByName(name);
        List<ProductDto> values = new ArrayList<>();

        if (!results.isEmpty()) {
            values = results
                    .stream()
                    .map(value -> modelMapper.map(value, ProductDto.class))
                    .toList();
        }

        return values;
    }

    @Override
    public List<ProductDto> getAll() {
        List<Product> results = repository.findAll();
        List<ProductDto> values = new ArrayList<>();

        if (!results.isEmpty()) {
            values = results
                    .stream()
                    .map(value -> modelMapper.map(value, ProductDto.class))
                    .toList();
        }

        return values;
    }

}
