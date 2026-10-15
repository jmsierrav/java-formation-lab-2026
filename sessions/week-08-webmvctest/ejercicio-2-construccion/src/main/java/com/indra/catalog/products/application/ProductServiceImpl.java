package com.indra.catalog.products.application;

import com.indra.catalog.products.domain.exception.ProductNotFoundException;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.domain.port.ProductRepository;
import java.util.concurrent.atomic.AtomicLong;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final AtomicLong sequence = new AtomicLong();

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product create(CreateProductCommand command) {
        Product product = new Product(
                "PRD-%04d".formatted(sequence.incrementAndGet()),
                command.name(),
                command.price(),
                command.stock());
        return repository.save(product);
    }

    @Override
    public Product findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public void delete(String id) {
        if (!repository.deleteById(id)) {
            throw new ProductNotFoundException(id);
        }
    }
}
