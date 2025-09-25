package com.leokenzley.bank_transactions.model.request;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
  @NotBlank(message = "Campo nome do cliente é obrigatório")
  String clientName
) {}
