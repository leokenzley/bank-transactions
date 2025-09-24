package com.leokenzley.bank_transactions.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class AuthenticationController {

  @GetMapping("/login")
  public String login() {
    return "/authentication/login";
  }

  @GetMapping("/init")
  public String init() {
    return "/init";
  }
}
