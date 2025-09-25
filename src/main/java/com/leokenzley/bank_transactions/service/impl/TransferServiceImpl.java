package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import com.leokenzley.bank_transactions.service.TransferService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class TransferServiceImpl implements TransferService {
  @Autowired
  private AccountRepository accountRepository;

  @Transactional
  @Override
  public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    log.info("Starting transfer from account {} to account {} with amount {}",
      fromAccountId, toAccountId, amount);

    AccountEntity fromAccount = accountRepository.findById(fromAccountId).orElseThrow(() ->
      new RuntimeException("Account not found: " + fromAccountId));

    AccountEntity toAccount = accountRepository.findById(toAccountId).orElseThrow(() -> new
      RuntimeException("Account not found: " + toAccountId));


    if (fromAccount.getBalance().compareTo(amount) < 0) {
      throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
    }

    // Debita
    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
    // Credita
    toAccount.setBalance(toAccount.getBalance().add(amount));

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
  }

  @Transactional
  @Override
  public void transferUpgrade(Long fromAccountId, Long toAccountId, BigDecimal amount) {
    log.info("Starting transfer from account {} to account {} with amount {}",
      fromAccountId, toAccountId, amount);

    AccountEntity fromAccount = accountRepository.findById(fromAccountId).orElseThrow(() ->
      new RuntimeException("Account not found: " + fromAccountId));

    AccountEntity toAccount = accountRepository.findById(toAccountId).orElseThrow(() -> new
      RuntimeException("Account not found: " + toAccountId));


    if (fromAccount.getBalance().compareTo(amount) < 0) {
      throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
    }

    // Debita
    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
    // Credita
    toAccount.setBalance(toAccount.getBalance().add(amount));

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
  }
}
