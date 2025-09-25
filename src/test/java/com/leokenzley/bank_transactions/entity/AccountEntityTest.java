package com.leokenzley.bank_transactions.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountEntityTest {

  private static Validator validator;

  @BeforeAll
  static void setupValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void deveCriarContaValida() {
    AccountEntity account = new AccountEntity(1L, "Cliente Teste", BigDecimal.valueOf(100));
    Set<ConstraintViolation<AccountEntity>> violations = validator.validate(account);
    assertTrue(violations.isEmpty(), "Conta válida não deve cair nas regras de validação");
  }

  @Test
  void deveFalharQuandoNomeClienteVazio() {
    AccountEntity account = new AccountEntity(1L, "", BigDecimal.valueOf(50));
    Set<ConstraintViolation<AccountEntity>> violations = validator.validate(account);
    assertFalse(violations.isEmpty(), "Deveria falhar com nome vazio");
    assertEquals("Nome do cliente é obrigatório",
      violations.iterator().next().getMessage());
  }

  @Test
  void deveFalharQuandoSaldoNegativo() {
    AccountEntity account = new AccountEntity(1L, "Cliente Negativo", BigDecimal.valueOf(-10));
    Set<ConstraintViolation<AccountEntity>> violations = validator.validate(account);
    assertFalse(violations.isEmpty(), "Deveria falhar com saldo negativo");
    assertEquals("Saldo inicial deve ser positivo",
      violations.iterator().next().getMessage());
  }

  @Test
  void saldoDefaultDeveSerZero() {
    AccountEntity account = new AccountEntity();
    assertEquals(BigDecimal.ZERO, account.getBalance(), "Saldo padrão deve ser ZERO");
  }

  @Test
  void deveAtualizarNomeESaldoComSetters() {
    AccountEntity account = new AccountEntity();
    account.setClientName("Novo Cliente");
    account.setBalance(BigDecimal.valueOf(200));

    assertEquals("Novo Cliente", account.getClientName());
    assertEquals(BigDecimal.valueOf(200), account.getBalance());
  }
}
