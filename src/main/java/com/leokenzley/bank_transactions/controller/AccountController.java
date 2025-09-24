package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class AccountController {

  @Autowired
  private AccountService accountService;

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/menu")
  public String menu() {
    return "menu";
  }

  // Admin: Create Account
  @GetMapping("/admin/create-account")
  public String showCreateAccountForm(Model model) {
    model.addAttribute("account", new AccountRequest());
    return "create-account";
  }

  @PostMapping("/admin/create-account")
  public String createAccount(
    @Valid @ModelAttribute("account") AccountRequest account,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes) {
    if (bindingResult.hasErrors()) {
      log.info("Form validation errors: {}", bindingResult.getAllErrors());
      return "create-account";
    }
    try {
      accountService.createAccount(account);
      redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso!");
      return "redirect:/menu";
    } catch (ConstraintViolationException e) {
      log.error("Database validation error: {}", e.getMessage());
      List<String> errors = new ArrayList<>();
      e.getConstraintViolations().forEach(violation ->
        errors.add(violation.getMessage()));
      model.addAttribute("errors", errors);
      model.addAttribute("account", account);
      return "create-account";
    }
  }

  // User: Credit
  @GetMapping("/user/credit")
  public String showCreditForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "credit";
  }
/**
  @PostMapping("/user/credit")
  public String credit(@RequestParam Long accountId, @RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
    try {
      accountService.credit(accountId, amount);
      redirectAttributes.addFlashAttribute("success", "Crédito realizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }

  // User: Debit
  @GetMapping("/user/debit")
  public String showDebitForm(Model model) {
    List<Account> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "debit";
  }

  @PostMapping("/user/debit")
  public String debit(@RequestParam Long accountId, @RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
    try {
      accountService.debit(accountId, amount);
      redirectAttributes.addFlashAttribute("success", "Débito realizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }

  // User: Transfer
  @GetMapping("/user/transfer")
  public String showTransferForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "transfer";
  }

  @PostMapping("/user/transfer")
  public String transfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
    try {
      accountService.transfer(fromAccountId, toAccountId, amount);
      redirectAttributes.addFlashAttribute("success", "Transferência realizada com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }*/
}
