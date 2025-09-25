package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.request.TransactionCreditRequest;
import com.leokenzley.bank_transactions.model.request.TransactionTransferRequest;
import com.leokenzley.bank_transactions.model.request.TransactionalDebitRequest;
import com.leokenzley.bank_transactions.model.response.AccountResponse;
import com.leokenzley.bank_transactions.service.AccountService;
import com.leokenzley.bank_transactions.service.TransactionService;
import com.leokenzley.bank_transactions.service.TransferService;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@RequestMapping("/transactions")
@Controller
public class TransactionController {
  @Autowired
  private TransactionService transactionService;
  @Autowired
  private AccountService accountService;

  @Autowired
  private TransferService transferService;

  @GetMapping("/credit")
  public String showCreditForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("transactionCredit", new TransactionCreditRequest(null, null));
    return "/transactions/credit";
  }

  @PostMapping("/credit")
  public String credit(
    @ModelAttribute("transactionCredit") TransactionCreditRequest credit,
    RedirectAttributes redirectAttributes,
    Model model) {
    try {
      transactionService.credit(credit);
      List<AccountResponse> accounts = accountService.getAllAccounts();
      model.addAttribute("accounts", accounts);
      redirectAttributes.addFlashAttribute("success", "Crédito realizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "/transactions/credit";
    }
    return "redirect:/init";
  }

  @GetMapping("/debit")
  public String showDebitForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("transactionalDebit", new TransactionalDebitRequest(null, null));
    return "transactions/debit";
  }

  @PostMapping("/debit")
  public String debit(
    @ModelAttribute("transactionDebit") TransactionalDebitRequest debit,
    RedirectAttributes redirectAttributes,
    Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("transactionalDebit", new TransactionalDebitRequest(null, null));
    try {
      transactionService.debit(debit);
      redirectAttributes.addFlashAttribute("success", "Débito realizado com sucesso!");
      return "redirect:/init";
    } catch (ConstraintViolationException e) {
      log.error("Database validation error: {}", e.getMessage());
      List<String> errors = new ArrayList<>();
      e.getConstraintViolations().forEach(violation ->
      errors.add(violation.getMessage()));
      model.addAttribute("errors", errors);
    }
    catch (Exception e) {
      model.addAttribute("errors", Arrays.asList(e.getMessage()));
      model.addAttribute("transactionalDebit", debit);
    }
    return "transactions/debit";
  }


  @GetMapping("/transfer")
  public String showTransferForm(Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    model.addAttribute("transactionTransfer", new TransactionTransferRequest(null, null, null));
    return "transactions/transfer";
  }

  @PostMapping("/transfer")
  public String transfer(
    @ModelAttribute("transactionTransfer") TransactionTransferRequest request,
    RedirectAttributes redirectAttributes,
    Model model) {
    List<AccountResponse> accounts = accountService.getAllAccounts();
    model.addAttribute("accounts", accounts);
    try {
      transactionService.transfer(request);
      redirectAttributes.addFlashAttribute("success", "Transferência realizada com sucesso!");
    } catch (Exception e) {
      model.addAttribute("errors", Arrays.asList(e.getMessage()));
      return "transactions/transfer";
    }
    return "redirect:/init";
  }
}
