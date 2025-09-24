package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import com.leokenzley.bank_transactions.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RequestMapping("/transactions")
public class TransactionController {
  @Autowired
  private TransactionService transactionService;
  @Autowired
  private AccountService accountService;

  @GetMapping("/credit")
  public String showCreditForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "credit";
  }

  @PostMapping("/credit")
  public String credit(
    @RequestParam String accountNumber,
    @RequestParam BigDecimal amount,
    RedirectAttributes redirectAttributes) {
    try {
      transactionService.credit(accountNumber, amount);
      redirectAttributes.addFlashAttribute("success", "Crédito realizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }

  // User: Debit
  @GetMapping("/debit")
  public String showDebitForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "debit";
  }

  @PostMapping("/debit")
  public String debit(@RequestParam String accountNumber, @RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
    try {
      transactionService.debit(accountNumber, amount);
      redirectAttributes.addFlashAttribute("success", "Débito realizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }

  // User: Transfer
  @GetMapping("/transfer")
  public String showTransferForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("formData", new HashMap<String, Object>());
    return "transfer";
  }

  @PostMapping("/transfer")
  public String transfer(@RequestParam String fromAccountId, @RequestParam String toAccountId, @RequestParam BigDecimal amount, RedirectAttributes redirectAttributes) {
    try {
      transactionService.transfer(fromAccountId, toAccountId, amount);
      redirectAttributes.addFlashAttribute("success", "Transferência realizada com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/menu";
  }
}
