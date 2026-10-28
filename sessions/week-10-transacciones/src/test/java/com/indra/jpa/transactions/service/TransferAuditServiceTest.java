package com.indra.jpa.transactions.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.indra.jpa.transactions.model.TransferEvent;
import com.indra.jpa.transactions.repository.TransferEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TransferAuditServiceTest {

    @Test
    @DisplayName("guarda el evento recibido en el repositorio")
    void savesTransferEvent() {
        TransferEventRepository repository = mock(TransferEventRepository.class);
        TransferAuditService service = new TransferAuditService(repository);
        TransferEvent event = new TransferEvent();

        service.log(event);

        verify(repository).save(event);
    }
}
