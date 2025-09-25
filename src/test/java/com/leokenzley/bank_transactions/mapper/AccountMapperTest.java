package com.leokenzley.bank_transactions.mapper;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {

  private AccountMapper accountMapper;

  @BeforeEach
  void setUp() {
    accountMapper = new AccountMapper();
  }

  @Test
  void toEntity_shouldReturnNull_whenRequestIsNull() {
    AccountEntity entity = accountMapper.toEntity(null);
    assertThat(entity).isNull();
  }

  @Test
  void toEntity_shouldMapClientNameCorrectly() {
    AccountRequest request = new AccountRequest("John Doe");
    AccountEntity entity = accountMapper.toEntity(request);

    assertThat(entity).isNotNull();
    assertThat(entity.getClientName()).isEqualTo("John Doe");
    assertThat(entity.getId()).isNull(); // id não é mapeado
    assertThat(entity.getBalance()).isEqualTo(BigDecimal.ZERO); // balance não é mapeado
  }

  @Test
  void toResponse_shouldReturnNull_whenEntityIsNull() {
    AccountResponse response = accountMapper.toResponse(null);
    assertThat(response).isNull();
  }

  @Test
  void toResponse_shouldMapAllFieldsCorrectly() {
    AccountEntity entity = new AccountEntity();
    entity.setId(1L);
    entity.setClientName("John Doe");
    entity.setBalance(BigDecimal.valueOf(1000));

    AccountResponse response = accountMapper.toResponse(entity);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getClientName()).isEqualTo("John Doe");
    assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
  }
}
