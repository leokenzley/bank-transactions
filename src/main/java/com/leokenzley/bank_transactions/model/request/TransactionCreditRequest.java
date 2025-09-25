package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record TransactionCreditRequest(
  Long accountId,
  @DecimalMin(value = "0.1", message = "O valor deve ser maior que zero")
  BigDecimal amount
) {}
