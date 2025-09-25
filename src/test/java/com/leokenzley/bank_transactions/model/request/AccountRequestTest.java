package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void deveSerValidoQuandoClientNamePreenchido() {
    var request = new AccountRequest("Cliente Teste");

    Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);

    assertTrue(violations.isEmpty(), "Não deveria haver erros de validação");
  }

  @Test
  void deveFalharQuandoClientNameForNulo() {
    var request = new AccountRequest(null);

    Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty(), "Deveria haver erro de validação");
    assertEquals("Campo nome do cliente é obrigatório",
      violations.iterator().next().getMessage());
  }

  @Test
  void deveFalharQuandoClientNameForVazio() {
    var request = new AccountRequest("");

    Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("Campo nome do cliente é obrigatório",
      violations.iterator().next().getMessage());
  }

  @Test
  void deveFalharQuandoClientNameForEspacos() {
    var request = new AccountRequest("   ");

    Set<ConstraintViolation<AccountRequest>> violations = validator.validate(request);

    assertFalse(violations.isEmpty());
    assertEquals("Campo nome do cliente é obrigatório",
      violations.iterator().next().getMessage());
  }

  @Test
  void devePermitirUsoDeGettersESetters() {
    var request = new AccountRequest("Novo Cliente");

    assertEquals("Novo Cliente", request.clientName());
  }
}
