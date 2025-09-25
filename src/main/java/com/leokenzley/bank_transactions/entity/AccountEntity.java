package com.leokenzley.bank_transactions.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tb_account")
public class AccountEntity {
  @Id
  @GeneratedValue
  private Long id;

  @NotBlank(message = "Nome do cliente é obrigatório")
  private String clientName;

  @Min(value = 0, message = "Saldo inicial deve ser positivo")
  private BigDecimal balance = BigDecimal.ZERO;
}
