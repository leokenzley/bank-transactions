package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
  @NotBlank(message = "Campo nome do cliente é obrigatório")
  private String clientName;
}
