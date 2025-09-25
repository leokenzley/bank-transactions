package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.service.AccountService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes para AccountController")
class AccountControllerTest {

  @Mock
  private AccountService accountService;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private AccountController controller;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Deve retornar o nome da view correta e adicionar um AccountRequest vazio ao model
   */
  @DisplayName("Testa o GET /create-account")
  @Test
  void testShowCreateAccountForm() {
    String viewName = controller.showCreateAccountForm(model);

    // Verifica se o model recebeu um atributo "account"
    verify(model, times(1)).addAttribute(eq("account"), any(AccountRequest.class));

    // Verifica se o nome da view está correto
    assertEquals("accounts/create-account", viewName);
  }

  /**
   * Deve retornar a mesma view do formulário sem chamar o service
   */
  @DisplayName("Testa o POST /create-account quando há erros de validação (BindingResult.hasErrors == true)")
  @Test
  void testCreateAccountWithValidationErrors() {
    AccountRequest request = new AccountRequest(null);
    when(bindingResult.hasErrors()).thenReturn(true);

    String viewName = controller.createAccount(request, bindingResult, model, redirectAttributes);

    // Verifica se o service NÃO foi chamado
    verify(accountService, never()).createAccount(any());

    // Verifica se retorna a view do formulário
    assertEquals("accounts/create-account", viewName);
  }

  /**
   * Deve capturar os erros, adicionar ao model e retornar a view do formulário
   */
  @DisplayName("Testa o POST /create-account simulando a exception ConstraintViolationException")
  @Test
  void testCreateAccountConstraintViolationExceptionWithErrors() {
    var request = new AccountRequest("Neo");

    when(bindingResult.hasErrors()).thenReturn(false);

    // Criando mock de ConstraintViolation
    ConstraintViolation<AccountRequest> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Campo nome inválido");

    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    when(exception.getConstraintViolations()).thenReturn(Collections.singleton(violation));

    doThrow(exception).when(accountService).createAccount(request);

    String viewName = controller.createAccount(request, bindingResult, model, redirectAttributes);

    // Verifica se a view do formulário foi retornada
    assertEquals("accounts/create-account", viewName);

    // Verifica se o model recebeu os atributos de erros e o account
    verify(model, times(1)).addAttribute(eq("errors"), any(List.class));
    verify(model, times(1)).addAttribute("account", request);
  }

  /**
   * Deve chamar o service, adicionar mensagem de sucesso e redirecionar
   */
  @DisplayName("Testa o POST /create-account quando a validação passa")
  @Test
  void testCreateAccountSuccess() {
    var request = new AccountRequest("Neo");

    when(bindingResult.hasErrors()).thenReturn(false);

    String viewName = controller.createAccount(request, bindingResult, model, redirectAttributes);

    // Verifica se o service foi chamado corretamente
    verify(accountService, times(1)).createAccount(request);

    // Verifica se a mensagem de sucesso foi adicionada
    verify(redirectAttributes, times(1)).addFlashAttribute("success", "Conta criada com sucesso!");

    // Verifica se o retorno é o redirect correto
    assertEquals("redirect:/init", viewName);
  }

  /**
   * Deve capturar os erros, adicionar ao model e retornar a view do formulário
   */
  @DisplayName("Testa o POST /create-account quando o service lança ConstraintViolationException")
  @Test
  void testCreateAccountConstraintViolationException() {
    var request = new AccountRequest("Neo");

    when(bindingResult.hasErrors()).thenReturn(false);

    // Simula ConstraintViolationException
    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    when(exception.getConstraintViolations()).thenReturn(Set.of());
    doThrow(exception).when(accountService).createAccount(request);

    String viewName = controller.createAccount(request, bindingResult, model, redirectAttributes);

    // Verifica se a view do formulário foi retornada
    assertEquals("accounts/create-account", viewName);

    // Verifica se o model recebeu os atributos de erros e o account
    verify(model, times(1)).addAttribute(eq("errors"), any(List.class));
    verify(model, times(1)).addAttribute("account", request);
  }
}
