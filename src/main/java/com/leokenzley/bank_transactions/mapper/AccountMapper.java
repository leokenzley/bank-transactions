package com.leokenzley.bank_transactions.mapper;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountEntity toEntity(AccountRequest request){
    var entity = new AccountEntity();
    entity.setAccountNumber(request.getAccountNumber());
    entity.setClientName(request.getClientName());
    entity.setBalance(request.getBalance());
    return entity;
  }

  public AccountResponse toResponse(AccountEntity entity){
    return AccountResponse.builder()
            .accountNumber(entity.getAccountNumber())
            .clientName(entity.getClientName())
            .balance(entity.getBalance())
            .build();
  }
}
