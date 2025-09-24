package com.leokenzley.bank_transactions.service;

import java.math.BigDecimal;

public interface TransactionService {
  void credit(String accountNumber, BigDecimal amount);
  void debit(String accountNumber, BigDecimal amount);
  void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
}
