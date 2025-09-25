package com.leokenzley.bank_transactions.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {

  private AuthenticationController controller;

  @BeforeEach
  void setUp() {
    // Instancia real, sem dependências externas
    controller = new AuthenticationController();
  }

  /**
   * Testa o GET /login
   * Deve retornar o nome da view "/authentication/login"
   */
  @Test
  void testLogin() {
    String viewName = controller.login();

    // Verifica se a view retornada está correta
    assertEquals("/authentication/login", viewName);
  }

  /**
   * Testa o GET /init
   * Deve retornar o nome da view "/init"
   */
  @Test
  void testInit() {
    String viewName = controller.init();

    // Verifica se a view retornada está correta
    assertEquals("/init", viewName);
  }
}
