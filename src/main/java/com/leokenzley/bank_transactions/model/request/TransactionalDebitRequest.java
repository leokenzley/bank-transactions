package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionalDebitRequest (
  @NotNull(message = "O ID da conta é obrigatória")
  Long accountId,
  @DecimalMin(value = "0.1", message = "O valor deve ser maior que zero")
  BigDecimal amount
) {}
