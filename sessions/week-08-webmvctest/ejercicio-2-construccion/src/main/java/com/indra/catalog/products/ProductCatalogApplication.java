package com.indra.catalog.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de partida del ejercicio 2.
 *
 * <p>Construye a partir de aquí el catálogo de productos siguiendo la arquitectura
 * descrita en {@code ejercicio-2-construccion/README.md}.
 */
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}
