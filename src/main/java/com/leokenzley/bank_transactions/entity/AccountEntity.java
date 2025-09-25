package com.leokenzley.bank_transactions.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tb_account")
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "balance >= 0")
public class AccountEntity {
  @Id
  @GeneratedValue
  private Long id;

  @NotBlank(message = "Nome do cliente é obrigatório")
  private String clientName;

  @Min(value = 0, message = "Saldo inicial deve ser positivo")
  private BigDecimal balance = BigDecimal.ZERO;
}
