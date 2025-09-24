package com.leokenzley.bank_transactions.service;

import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;

import java.util.List;

public interface AccountService {
  void createAccount(AccountRequest request);
  void deleteAccount(String accountNumber);
  List<AccountResponse> getAllAccounts();
  AccountResponse getAccountById(Long accountNumber);
}
