package com.leokenzley.bank_transactions.service;

import java.math.BigDecimal;

public interface TransferService {
  void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount);
  void transferUpgrade(Long fromAccountId, Long toAccountId, BigDecimal amount);
}
