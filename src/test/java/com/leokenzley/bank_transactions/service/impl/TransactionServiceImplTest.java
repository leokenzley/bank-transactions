package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.model.request.TransactionCreditRequest;
import com.leokenzley.bank_transactions.model.request.TransactionTransferRequest;
import com.leokenzley.bank_transactions.model.request.TransactionalDebitRequest;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

  @InjectMocks
  private TransactionServiceImpl service;

  @Mock
  private AccountRepository repository;

  private AccountEntity account1;
  private AccountEntity account2;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    account1 = new AccountEntity();
    account1.setId(1L);
    account1.setClientName("Neo");
    account1.setBalance(BigDecimal.valueOf(1000));

    account2 = new AccountEntity();
    account2.setId(2L);
    account2.setClientName("Trinity");
    account2.setBalance(BigDecimal.valueOf(500));
  }

  // ==================== CREDIT ====================

  @Test
  void testCreditSuccess() {
    TransactionCreditRequest request = new TransactionCreditRequest(1L, BigDecimal.valueOf(100));
    when(repository.findById(1L)).thenReturn(Optional.of(account1));
    when(repository.save(account1)).thenReturn(account1);

    service.credit(request);

    assertEquals(BigDecimal.valueOf(1100), account1.getBalance());
    verify(repository).findById(1L);
    verify(repository).save(account1);
  }

  @Test
  void testCreditAccountNotFound() {
    TransactionCreditRequest request = new TransactionCreditRequest(1L, BigDecimal.valueOf(100));
    when(repository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.credit(request));
    assertEquals("Conta não encontrada", ex.getMessage());
  }

  // ==================== DEBIT ====================

  @Test
  void testDebitSuccess() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(200));
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.of(account1));
    when(repository.save(account1)).thenReturn(account1);

    service.debit(request);

    assertEquals(BigDecimal.valueOf(800), account1.getBalance());
    verify(repository).findByAccountIdPessimistic(1L); // garante que o lock foi solicitado
    verify(repository).save(account1);
  }

  @Test
  void testDebitInsufficientBalance() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(2000));
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.of(account1));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.debit(request));
    assertEquals("Saldo insuficiente", ex.getMessage());
  }

  @Test
  void testDebitAccountNotFound() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(100));
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.debit(request));
    assertEquals("Conta não encontrada", ex.getMessage());
  }

  // ==================== TRANSFER ====================

  @Test
  void testTransferSuccess() {
    TransactionTransferRequest request = new TransactionTransferRequest(1L, 2L, BigDecimal.valueOf(300));

    // Simula lock pessimista
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.of(account1));
    when(repository.findByAccountIdPessimistic(2L)).thenReturn(Optional.of(account2));
    when(repository.save(any(AccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

    service.transfer(request);

    assertEquals(BigDecimal.valueOf(700), account1.getBalance());
    assertEquals(BigDecimal.valueOf(800), account2.getBalance());

    // Verifica que os locks foram chamados na ordem correta
    InOrder inOrder = inOrder(repository);
    inOrder.verify(repository).findByAccountIdPessimistic(1L);
    inOrder.verify(repository).findByAccountIdPessimistic(2L);
    inOrder.verify(repository).save(account1);
    inOrder.verify(repository).save(account2);
  }

  @Test
  void testTransferInsufficientBalance() {
    TransactionTransferRequest request = new TransactionTransferRequest(1L, 2L, BigDecimal.valueOf(2000));
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.of(account1));
    when(repository.findByAccountIdPessimistic(2L)).thenReturn(Optional.of(account2));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> service.transfer(request));
    assertEquals("Saldo insuficiente", ex.getMessage());
  }

  @Test
  void testTransferAccountNotFound() {
    TransactionTransferRequest request = new TransactionTransferRequest(1L, 2L, BigDecimal.valueOf(100));
    when(repository.findByAccountIdPessimistic(1L)).thenReturn(Optional.empty());
    when(repository.findByAccountIdPessimistic(2L)).thenReturn(Optional.of(account2));

    assertThrows(RuntimeException.class, () -> service.transfer(request));
  }
}
