package com.indra.catalog.products.infrastructure;

import com.indra.catalog.products.application.ProductService;
import com.indra.catalog.products.application.ProductServiceImpl;
import com.indra.catalog.products.domain.port.ProductRepository;
import com.indra.catalog.products.infrastructure.persistence.InMemoryProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfiguration {

    @Bean
    ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    ProductService productService(ProductRepository repository) {
        return new ProductServiceImpl(repository);
    }
}
