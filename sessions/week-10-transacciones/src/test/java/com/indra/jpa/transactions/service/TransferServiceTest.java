package com.indra.jpa.transactions.service;

import com.indra.jpa.transactions.exception.InsufficientFundsException;
import com.indra.jpa.transactions.model.Account;
import com.indra.jpa.transactions.model.TransferEvent;
import com.indra.jpa.transactions.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    private AccountRepository accountRepository;
    private TransferAuditService transferAuditService;
    private TransferService transferService;
    private Account source;
    private Account destination;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transferAuditService = mock(TransferAuditService.class);
        transferService = new TransferService(accountRepository, transferAuditService);
        source = account(1L, "100.00");
        destination = account(2L, "50.00");
    }

    @Test
    @DisplayName("transfiere el saldo y registra el evento de auditoría")
    void transfersAmountAndLogsAuditEvent() throws InsufficientFundsException {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));

        transferService.transfer(1L, 2L, new BigDecimal("25.00"));

        assertEquals(new BigDecimal("75.00"), source.getBalance());
        assertEquals(new BigDecimal("75.00"), destination.getBalance());
        verify(transferAuditService).log(argThat(event ->
                event.getFromAccountId().equals(1L)
                        && event.getToAccountId().equals(2L)
                        && event.getAmount().compareTo(new BigDecimal("25.00")) == 0));
        verify(accountRepository).saveAndFlush(source);
        verify(accountRepository).save(destination);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0.00", "-0.01"})
    @DisplayName("rechaza montos nulos, cero o negativos")
    void rejectsNonPositiveAmounts(BigDecimal amount) {
        assertThrows(IllegalArgumentException.class, () -> transferService.transfer(1L, 2L, amount));

        verifyNoInteractions(accountRepository, transferAuditService);
    }

    @ParameterizedTest
    @CsvSource(value = {"1,1", "null,2", "1,null"}, nullValues = "null")
    @DisplayName("rechaza identificadores nulos o cuentas iguales")
    void rejectsInvalidAccountIds(Long sourceId, Long destinationId) {
        assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(sourceId, destinationId, BigDecimal.ONE));

        verifyNoInteractions(accountRepository, transferAuditService);
    }

    @Test
    @DisplayName("falla cuando la cuenta de origen no existe")
    void failsWhenSourceAccountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, BigDecimal.ONE));

        assertEquals("Source account not found", exception.getMessage());
        verify(accountRepository).findById(1L);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transferAuditService);
    }

    @Test
    @DisplayName("falla cuando la cuenta de destino no existe")
    void failsWhenDestinationAccountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(1L, 2L, BigDecimal.ONE));

        assertEquals("Destination account not found", exception.getMessage());
        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(transferAuditService);
    }

    @Test
    @DisplayName("lanza excepción por fondos insuficientes después de registrar auditoría")
    void rejectsTransferWithInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));

        assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("150.00")));

        assertEquals(new BigDecimal("-50.00"), source.getBalance());
        assertEquals(new BigDecimal("50.00"), destination.getBalance());
        verify(transferAuditService).log(any(TransferEvent.class));
        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository).saveAndFlush(source);
        verifyNoMoreInteractions(accountRepository);
    }

    private Account account(Long id, String balance) {
        Account account = new Account();
        account.setId(id);
        account.setBalance(new BigDecimal(balance));
        return account;
    }
}
