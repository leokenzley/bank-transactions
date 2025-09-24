package com.leokenzley.bank_transactions.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AccountResponse {
  private Long id;
  private String accountNumber;
  private String clientName;
  private BigDecimal balance;
}
