package com.leokenzley.bank_transactions.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {
  private String clientName;
  private String accountNumber;
  private BigDecimal initialBalance;
}
