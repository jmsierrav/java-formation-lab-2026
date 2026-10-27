package com.indra.jpa.transactions.service;

import com.indra.jpa.transactions.model.TransferEvent;
import com.indra.jpa.transactions.repository.TransferEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service 
public class TransferAuditService {

    private final TransferEventRepository transferEventRepository;

    @Autowired
    public TransferAuditService(TransferEventRepository transferEventRepository) {
        this.transferEventRepository = transferEventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(TransferEvent event) {
        transferEventRepository.save(event);
    }
}
