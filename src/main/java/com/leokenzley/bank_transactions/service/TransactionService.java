package com.leokenzley.bank_transactions.service;

import com.leokenzley.bank_transactions.model.request.TransactionCreditRequest;
import com.leokenzley.bank_transactions.model.request.TransactionTransferRequest;
import com.leokenzley.bank_transactions.model.request.TransactionalDebitRequest;

public interface TransactionService {
  void credit(TransactionCreditRequest credit);
  void debit(TransactionalDebitRequest request);
  void transfer(TransactionTransferRequest request);
}
