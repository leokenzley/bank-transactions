package com.leokenzley.bank_transactions.model.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountResponseTest {

  @Test
  void deveCriarAccountResponseComBuilder() {
    AccountResponse response = AccountResponse.builder()
      .id(1L)
      .clientName("Cliente Teste")
      .balance(BigDecimal.valueOf(100))
      .build();

    assertEquals(1L, response.getId());
    assertEquals("Cliente Teste", response.getClientName());
    assertEquals(BigDecimal.valueOf(100), response.getBalance());
  }

  @Test
  void deveCriarAccountResponseComSetters() {
    AccountResponse response = AccountResponse
      .builder()
      .id(2L)
      .clientName("Outro Cliente")
      .balance(BigDecimal.valueOf(200))
      .build();

    assertEquals(2L, response.getId());
    assertEquals("Outro Cliente", response.getClientName());
    assertEquals(BigDecimal.valueOf(200), response.getBalance());
  }

  @Test
  void deveAceitarBalanceNulo() {
    AccountResponse response = AccountResponse.builder()
      .id(3L)
      .clientName("Cliente Sem Saldo")
      .balance(null)
      .build();

    assertNull(response.getBalance());
    assertEquals("Cliente Sem Saldo", response.getClientName());
  }

  @Test
  void deveAtualizarBalanceComSetter() {
    AccountResponse response = AccountResponse.builder()
      .id(4L)
      .clientName("Cliente Atualizável")
      .balance(BigDecimal.valueOf(500))
      .build();

    response.setBalance(BigDecimal.valueOf(800));

    assertEquals(BigDecimal.valueOf(800), response.getBalance());
  }
}
