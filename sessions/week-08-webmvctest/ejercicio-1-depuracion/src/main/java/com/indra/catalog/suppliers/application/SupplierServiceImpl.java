package com.indra.catalog.suppliers.application;

import com.indra.catalog.suppliers.domain.Supplier;
import com.indra.catalog.suppliers.domain.SupplierNotFoundException;
import com.indra.catalog.suppliers.domain.SupplierRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository repository;
    private final AtomicLong sequence = new AtomicLong();

    public SupplierServiceImpl(SupplierRepository repository) {
        this.repository = repository;
    }

    @Override
    public Supplier create(CreateSupplierCommand command) {
        String name = command.name() == null ? null : command.name().trim().toUpperCase(Locale.ROOT);
        String taxId = command.taxId() == null ? null : command.taxId().replace(".", "").trim();
        Supplier supplier = new Supplier(
                "SUP-%03d".formatted(sequence.incrementAndGet()),
                name, taxId, command.email(), "Creado el " + LocalDateTime.now());
        return repository.save(supplier);
    }

    @Override
    public Supplier findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new SupplierNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
