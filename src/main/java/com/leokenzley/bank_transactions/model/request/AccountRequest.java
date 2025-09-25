package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AccountRequest(
  @NotBlank(message = "Campo nome do cliente é obrigatório")
  @NotEmpty(message = "Campo nome do cliente é obrigatório")
  String clientName
) {}
