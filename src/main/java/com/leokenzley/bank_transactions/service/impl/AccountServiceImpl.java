package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

  @Override
  public void createAccount(AccountRequest request) {

  }

  @Override
  public void deleteAccount(String accountNumber) {

  }

  @Override
  public List<AccountResponse> getAllAccounts() {
    return List.of();
  }

  @Override
  public AccountResponse getAccountById(Long accountNumber) {
    return null;
  }
}
