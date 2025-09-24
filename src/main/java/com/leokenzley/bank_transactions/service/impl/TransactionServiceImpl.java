package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.service.AccountService;
import com.leokenzley.bank_transactions.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {
  @Autowired
  private AccountService accountService;

  @Override
  public void credit(String accountNumber, BigDecimal amount) {

  }

  @Override
  public void debit(String accountNumber, BigDecimal amount) {

  }

  @Override
  public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {

  }
}
