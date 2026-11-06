package com.indra.jpa.transactions.repository;

import com.indra.jpa.transactions.model.TransferEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferEventRepository extends JpaRepository<TransferEvent, Long> {
}