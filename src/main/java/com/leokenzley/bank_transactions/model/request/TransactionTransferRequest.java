package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransactionTransferRequest(
  @NotNull(message = "O ID da conta de origem é obrigatória")
  Long fromAccountId,
  @NotNull(message = "O ID da conta de destino é obrigatória")
  Long toAccountId,
  //@DecimalMin(value = "0.1", message = "O valor da transferência ser maior que zero")
  BigDecimal amount
) {
}
