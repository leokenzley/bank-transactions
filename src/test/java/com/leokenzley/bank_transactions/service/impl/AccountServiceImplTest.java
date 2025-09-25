package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.mapper.AccountMapper;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para implementação de AccountService")
class AccountServiceImplTest {

  public static final String CLIENT_NAME_NEO = "Neo";
  public static final String CLIENT_NAME_LEO = "Leo";
  public static final BigDecimal BALANCE_100 = BigDecimal.valueOf(100.00);
  public static final BigDecimal BALANCE_200 = BigDecimal.valueOf(200.00);

  @Mock
  private AccountMapper mapper;

  @Mock
  private AccountRepository repository;

  @InjectMocks
  private AccountServiceImpl service;

  private AutoCloseable closeable;
  private Validator validator;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @DisplayName("Testa criação de conta com saldo zerado")
  @Test
  void testCreateAccount() {
    AccountRequest request = new AccountRequest(CLIENT_NAME_NEO);
    AccountEntity accountEntity = new AccountEntity(null, CLIENT_NAME_NEO, BigDecimal.ZERO);
    when(mapper.toEntity(request)).thenReturn(accountEntity);

    service.createAccount(request);

    assertEquals(BigDecimal.ZERO, accountEntity.getBalance());
    verify(repository, times(1)).save(accountEntity);
    verify(mapper, times(1)).toEntity(request);
  }

  @DisplayName("Testa retorno de todas as contas")
  @Test
  void testGetAllAccounts() {
    AccountEntity account1 = new AccountEntity(1L, CLIENT_NAME_NEO, BALANCE_100);
    AccountEntity account2 = new AccountEntity(2L, CLIENT_NAME_LEO, BALANCE_200);
    List<AccountEntity> accounts = Arrays.asList(account1, account2);

    when(repository.findAll(any(Sort.class))).thenReturn(accounts);

    when(mapper.toResponse(account1)).thenReturn(
      AccountResponse.builder()
        .id(1L)
        .clientName(CLIENT_NAME_NEO)
        .balance(BALANCE_100)
        .build()
    );
    when(mapper.toResponse(account2)).thenReturn(
      AccountResponse.builder()
        .id(2L)
        .clientName(CLIENT_NAME_LEO)
        .balance(BALANCE_200)
        .build()
    );

    List<AccountResponse> responses = service.getAllAccounts();

    assertEquals(2, responses.size());
    assertEquals(CLIENT_NAME_NEO, responses.get(0).getClientName());
    assertEquals(BALANCE_100, responses.get(0).getBalance());
    assertEquals(CLIENT_NAME_LEO, responses.get(1).getClientName());
    assertEquals(BALANCE_200, responses.get(1).getBalance());

    verify(repository, times(1)).findAll(any(Sort.class));
    verify(mapper, times(2)).toResponse(any(AccountEntity.class));
  }

  @DisplayName("Testa retorno de lista vazia quando não há contas")
  @Test
  void testGetAllAccountsEmptyList() {
    when(repository.findAll(any(Sort.class))).thenReturn(List.of());

    List<AccountResponse> responses = service.getAllAccounts();

    assertTrue(responses.isEmpty());
    verify(repository, times(1)).findAll(any(Sort.class));
    verify(mapper, never()).toResponse(any(AccountEntity.class));
  }
}