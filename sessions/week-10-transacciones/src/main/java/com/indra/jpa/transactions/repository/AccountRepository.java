package com.indra.jpa.transactions.repository;

import com.indra.jpa.transactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
