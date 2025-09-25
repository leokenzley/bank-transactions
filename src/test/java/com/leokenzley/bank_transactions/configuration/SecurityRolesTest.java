package com.leokenzley.bank_transactions.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityRolesTest {

  @BeforeEach
  void setUp() {
    // Limpa o contexto antes de cada teste
    SecurityContextHolder.clearContext();
  }

  @Test
  void testUserHasAdminRole() {
    // Simula um usuário autenticado com ROLE_ADMIN
    Authentication auth = new TestingAuthenticationToken("admin", "admin", "ROLE_ADMIN");
    SecurityContextHolder.getContext().setAuthentication(auth);

    // Recupera autenticação atual
    Authentication current = SecurityContextHolder.getContext().getAuthentication();

    assertNotNull(current);
    assertTrue(current.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    assertFalse(current.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
  }

  @Test
  void testUserHasUserRole() {
    Authentication auth = new TestingAuthenticationToken("user", "user", "ROLE_USER");
    SecurityContextHolder.getContext().setAuthentication(auth);

    Authentication current = SecurityContextHolder.getContext().getAuthentication();

    assertTrue(current.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    assertFalse(current.getAuthorities().stream()
      .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
  }
}
