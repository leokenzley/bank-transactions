package com.leokenzley.bank_transactions.controller;

import com.leokenzley.bank_transactions.model.request.AccountRequest;
import com.leokenzley.bank_transactions.service.AccountService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private AccountService accountService;

  @GetMapping("/create-account")
  public String showCreateAccountForm(Model model) {
    model.addAttribute("account", new AccountRequest(null));
    return "accounts/create-account";
  }

  @PostMapping("/create-account")
  public String createAccount(
    @Valid @ModelAttribute("account") AccountRequest account,
    BindingResult bindingResult,
    Model model,
    RedirectAttributes redirectAttributes) {
    List<String> errors = new ArrayList<>();
    if (bindingResult.hasErrors()) {
      log.info("Campos do formulário com erros de validação: {}", bindingResult.getAllErrors());
      errors.add("Por favor, verifique os campos do formulário.");
      model.addAttribute("errors", errors);
      return "accounts/create-account";
    }
    try {
      accountService.createAccount(account);
      redirectAttributes.addFlashAttribute("success", "Conta criada com sucesso!");
      return "redirect:/init";
    } catch (ConstraintViolationException e) {
      log.error("Erros na validação dos campos do banco: {}", e.getMessage());
      e.getConstraintViolations().forEach(violation ->
        errors.add(violation.getMessage()));
      model.addAttribute("errors", errors);
      model.addAttribute("account", account);
      return "accounts/create-account";
    }
  }

}
