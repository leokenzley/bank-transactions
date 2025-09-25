package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.request.TransactionCreditRequest;
import com.leokenzley.bank_transactions.model.request.TransactionTransferRequest;
import com.leokenzley.bank_transactions.model.request.TransactionalDebitRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import com.leokenzley.bank_transactions.service.TransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

  @InjectMocks
  private TransactionController controller;

  @Mock
  private TransactionService transactionService;

  @Mock
  private AccountService accountService;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  private AccountResponse account1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    account1 =  AccountResponse
      .builder()
      .id(1L)
      .clientName("Neo")
      .balance(BigDecimal.valueOf(1000))
      .build();
  }

  // ==================== GET FORMS ====================

  @Test
  void testShowCreditForm() {
    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.showCreditForm(model);

    verify(model).addAttribute("accounts", List.of(account1));
    verify(model).addAttribute(eq("transactionCredit"), any(TransactionCreditRequest.class));
    assertEquals("/transactions/credit", view);
  }

  @Test
  void testShowDebitForm() {
    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.showDebitForm(model);

    verify(model).addAttribute("accounts", List.of(account1));
    verify(model).addAttribute(eq("transactionalDebit"), any(TransactionalDebitRequest.class));
    assertEquals("transactions/debit", view);
  }

  @Test
  void testShowTransferForm() {
    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.showTransferForm(model);

    verify(model).addAttribute("accounts", List.of(account1));
    verify(model).addAttribute(eq("formData"), any());
    assertEquals("transactions/transfer", view);
  }

  // ==================== POST METHODS - SUCCESS ====================

  @Test
  void testCreditSuccess() {
    TransactionCreditRequest request = new TransactionCreditRequest(1L, BigDecimal.valueOf(100));

    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.credit(request, redirectAttributes, model);

    verify(transactionService).credit(request);
    verify(redirectAttributes).addFlashAttribute("success", "Crédito realizado com sucesso!");
    verify(model).addAttribute("accounts", List.of(account1));
    assertEquals("redirect:/init", view);
  }

  @Test
  void testDebitSuccess() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(50));

    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.debit(request, redirectAttributes, model);

    verify(transactionService).debit(request);
    verify(redirectAttributes).addFlashAttribute("success", "Débito realizado com sucesso!");
    verify(model).addAttribute("accounts", List.of(account1));
    assertEquals("redirect:/init", view);
  }

  @Test
  void testTransferSuccess() {
    TransactionTransferRequest request = new TransactionTransferRequest(1L, 2L, BigDecimal.valueOf(200));

    when(accountService.getAllAccounts()).thenReturn(List.of(account1));

    String view = controller.transfer(request, redirectAttributes, model);

    verify(transactionService).transfer(request);
    verify(redirectAttributes).addFlashAttribute("success", "Transferência realizada com sucesso!");
    verify(model).addAttribute("accounts", List.of(account1));
    assertEquals("redirect:/init", view);
  }

  // ==================== POST METHODS - EXCEPTIONS ====================

  @Test
  void testCreditException() {
    TransactionCreditRequest request = new TransactionCreditRequest(1L, BigDecimal.valueOf(100));
    doThrow(new RuntimeException("Erro")).when(transactionService).credit(request);

    String view = controller.credit(request, redirectAttributes, model);

    verify(redirectAttributes).addFlashAttribute("error", "Erro");
    assertEquals("/transactions/credit", view);
  }

  @Test
  void testDebitConstraintViolationException() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(50));

    ConstraintViolation<?> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Saldo insuficiente");
    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    when(exception.getConstraintViolations()).thenReturn(Collections.singleton(violation));

    doThrow(exception).when(transactionService).debit(request);

    String view = controller.debit(request, redirectAttributes, model);

    verify(model).addAttribute(eq("errors"), any(List.class));
    assertEquals("transactions/debit", view);
  }

  @Test
  void testDebitGenericException() {
    TransactionalDebitRequest request = new TransactionalDebitRequest(1L, BigDecimal.valueOf(50));
    doThrow(new RuntimeException("Erro genérico")).when(transactionService).debit(request);

    String view = controller.debit(request, redirectAttributes, model);

    verify(redirectAttributes).addFlashAttribute("error", "Erro genérico");
    assertEquals("transactions/debit", view);
  }

  @Test
  void testTransferException() {
    TransactionTransferRequest request = new TransactionTransferRequest(1L, 2L, BigDecimal.valueOf(200));
    doThrow(new RuntimeException("Erro transferência")).when(transactionService).transfer(request);

    String view = controller.transfer(request, redirectAttributes, model);

    verify(redirectAttributes).addFlashAttribute("error", "Erro transferência");
    assertEquals("transactions/transfer", view);
  }
}