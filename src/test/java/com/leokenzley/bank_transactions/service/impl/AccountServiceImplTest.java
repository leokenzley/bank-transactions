package com.leokenzley.bank_transactions.service.impl;

import com.leokenzley.bank_transactions.entity.AccountEntity;
import com.leokenzley.bank_transactions.mapper.AccountMapper;
import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TODO Essa classe de testes pode ser melhorada utilizando o Instâncio para gerar algumas massas de dados
 */
@DisplayName("Testes para implementação de AccountService")
class AccountServiceImplTest {

  public static final String CLIENT_NAME_NEO = "Neo";
  public static final String CLIENT_NAME_LEO = "Leo";
  @Mock
  private AccountMapper mapper;

  @Mock
  private AccountRepository repository;

  @InjectMocks
  private AccountServiceImpl service;

  // Inicializa os mocks antes de cada teste, meu repositório e meu mapper
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("Testa criação de conta com saldo zerado")
  @Test
  void testCreateAccount() {
    AccountRequest request = new AccountRequest();
    request.setClientName(CLIENT_NAME_NEO);

    var accountEntity = new AccountEntity();
    when(mapper.toEntity(request)).thenReturn(accountEntity);

    service.createAccount(request);

    // Verifica se o saldo foi zerado
    assertEquals(BigDecimal.ZERO, accountEntity.getBalance());

    // Verifica se o repository.save foi chamado com a entidade
    verify(repository, times(1)).save(accountEntity);
  }

  @DisplayName("Testa retorno de todas as contas")
  @Test
  void testGetAllAccounts() {
    var account1 = new AccountEntity();
    account1.setId(1L);
    account1.setClientName(CLIENT_NAME_NEO);
    account1.setBalance(BigDecimal.valueOf(100));

    var account2 = new AccountEntity();
    account2.setId(2L);
    account2.setClientName(CLIENT_NAME_LEO);
    account2.setBalance(BigDecimal.valueOf(200));

    when(repository.findAll()).thenReturn(Arrays.asList(account1, account2));

    AccountResponse response1 =  AccountResponse
      .builder()
      .id(1L)
      .clientName(CLIENT_NAME_NEO)
      .balance(BigDecimal.valueOf(100))
      .build();

    AccountResponse response2 = AccountResponse
      .builder()
      .id(2L)
      .clientName(CLIENT_NAME_LEO)
      .balance(BigDecimal.valueOf(200))
      .build();

    when(mapper.toResponse(account1)).thenReturn(response1);
    when(mapper.toResponse(account2)).thenReturn(response2);

    List<AccountResponse> responses = service.getAllAccounts();

    // Verifica tamanho da lista
    assertEquals(2, responses.size());

    // Verifica conteúdos
    assertEquals(CLIENT_NAME_NEO, responses.get(0).getClientName());
    assertEquals(BigDecimal.valueOf(100), responses.get(0).getBalance());

    assertEquals(CLIENT_NAME_LEO, responses.get(1).getClientName());
    assertEquals(BigDecimal.valueOf(200), responses.get(1).getBalance());

    // Verifica se os mocks foram chamados
    verify(repository, times(1)).findAll(); // Verifica se o repository.findAll foi chamado
    verify(mapper, times(1)).toResponse(account1); // Verifica se o mapper.toResponse foi chamado para account1
    verify(mapper, times(1)).toResponse(account2); // Verifica se o mapper.toResponse foi chamado para account2
  }
}
