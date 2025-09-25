package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {

  @InjectMocks
  private AuthenticationController controller; // Agora o mock é injetado aqui

  @Mock
  private Model model;

  @Mock
  private AccountService accountService;

  @BeforeEach
  void setUp() {
    // Instancia real, sem dependências externas
    controller = new AuthenticationController();
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Testa o GET /login
   * Deve retornar o nome da view "/authentication/login"
   */
  @Test
  void testLogin() {
    String viewName = controller.login();
    assertEquals("/authentication/login", viewName);
  }

  /**
   * Testa o GET /init
   * Deve retornar o nome da view "/init"
   */
  @Test
  void testInit() {
    List<AccountResponse> accounts = List.of(
      AccountResponse.builder().id(1L).clientName("Leo").balance(BigDecimal.TEN).build()
    );
    when(accountService.getAllAccounts()).thenReturn(accounts);
    String viewName = controller.init(model);
    assertEquals("/init", viewName);
  }
}
