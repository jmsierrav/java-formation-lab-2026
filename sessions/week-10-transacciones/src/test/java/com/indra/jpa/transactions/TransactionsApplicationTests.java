package com.indra.jpa.transactions;

import com.indra.jpa.transactions.exception.InsufficientFundsException;
import com.indra.jpa.transactions.model.Account;
import com.indra.jpa.transactions.repository.AccountRepository;
import com.indra.jpa.transactions.repository.TransferEventRepository;
import com.indra.jpa.transactions.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
		properties = "spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true")
class TransactionsApplicationTests {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransferEventRepository transferEventRepository;

	@Autowired
	private TransferService transferService;

	private Account source;
	private Account destination;

	@BeforeEach
	void setUp() {
		transferEventRepository.deleteAll();
		accountRepository.deleteAll();
		source = createAccount("Alice", "100.00");
		destination = createAccount("Bob", "50.00");
	}

	@ParameterizedTest
	@ValueSource(strings = {"100.01", "150.00"})
	@DisplayName("revierte los saldos y conserva la auditoría cuando no hay fondos suficientes")
	void insufficientFundsRollsBackBothBalancesAndKeepsAudit(String amount) {
		assertThrows(InsufficientFundsException.class,
				() -> transferService.transfer(source.getId(), destination.getId(), new BigDecimal(amount)));

		assertBalance(source.getId(), "100.00");
		assertBalance(destination.getId(), "50.00");
		assertThat(transferEventRepository.count()).isEqualTo(1);
	}

	@ParameterizedTest
	@CsvSource({
			"25.00, 75.00, 75.00",
			"40.00, 60.00, 90.00"
	})
	@DisplayName("actualiza ambos saldos después de una transferencia exitosa")
	void successfulTransferUpdatesBothBalances(String amount, String expectedSourceBalance,
			String expectedDestinationBalance) throws InsufficientFundsException {
		transferService.transfer(source.getId(), destination.getId(), new BigDecimal(amount));

		assertBalance(source.getId(), expectedSourceBalance);
		assertBalance(destination.getId(), expectedDestinationBalance);
	}

	@Test
	@DisplayName("registra un evento de auditoría para una transferencia exitosa")
	void successfulTransferCreatesAuditEvent() throws InsufficientFundsException {
		transferService.transfer(source.getId(), destination.getId(), new BigDecimal("25.00"));

		assertThat(transferEventRepository.count()).isEqualTo(1);
	}

	private Account createAccount(String owner, String balance) {
		Account account = new Account();
		account.setOwner(owner);
		account.setBalance(new BigDecimal(balance));
		return accountRepository.saveAndFlush(account);
	}

	private void assertBalance(Long accountId, String expectedBalance) {
		assertThat(accountRepository.findById(accountId).orElseThrow().getBalance())
				.isEqualByComparingTo(expectedBalance);
	}
}
