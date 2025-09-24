package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.mapper.AccountMapper;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import com.leokenzley.bank_transactions.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountMapper mapper;

  @Autowired
  private AccountRepository repository;

  @Override
  public void createAccount(AccountRequest request) {
    var account = mapper.toEntity(request);
    account.setBalance(BigDecimal.ZERO);
    repository.save(account);
  }

  @Override
  public List<AccountResponse> getAllAccounts() {
    return repository.findAll().stream().map(mapper::toResponse).toList();
  }

  @Override
  public AccountResponse getByAccountNumber(String accountNumber) {
    return repository
      .findByAccountNumber(accountNumber)
      .map(mapper::toResponse)
      .orElseThrow(()-> new RuntimeException("Conta não encontrada"));
  }
}
