package com.leokenzley.bank_transactions.mapper;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountEntity toEntity(AccountRequest request){
    var entity = new AccountEntity();
    entity.setClientName(request.clientName());
    return entity;
  }

  public AccountResponse toResponse(AccountEntity entity){
    return AccountResponse.builder()
            .clientName(entity.getClientName())
            .balance(entity.getBalance())
            .id(entity.getId())
            .build();
  }
}
