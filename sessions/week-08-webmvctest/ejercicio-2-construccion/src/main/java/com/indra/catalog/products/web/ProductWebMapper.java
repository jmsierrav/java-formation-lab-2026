package com.indra.catalog.products.web;

import com.indra.catalog.products.application.CreateProductCommand;
import com.indra.catalog.products.domain.model.Product;
import com.indra.catalog.products.web.dto.CreateProductRequest;
import com.indra.catalog.products.web.dto.ProductResponse;

final class ProductWebMapper {

    private ProductWebMapper() {
    }

    static CreateProductCommand toCommand(CreateProductRequest request) {
        return new CreateProductCommand(request.name(), request.price(), request.stock());
    }

    static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.id(), product.name(), product.price(), product.stock());
    }
}
