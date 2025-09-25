package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class AuthenticationController {
  @Autowired
  private AccountService accountService;

  @GetMapping("/login")
  public String login() {
    return "/authentication/login";
  }

  @GetMapping({"/init", "/"})
  public String init(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    return "/init";
  }

  @GetMapping("/logout")
  public String logout() {
    return "/authentication/login";
  }
}
