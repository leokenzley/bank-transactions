package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {
  @NotBlank(message = "Campo nome do cliente é obrigatório")
  private String clientName;
  @NotBlank(message = "Campo número da conta é obrigatório")
  private String accountNumber;
  @NotNull(message = "Campo saldo inicial é obrigatório")
  private BigDecimal balance;
}
