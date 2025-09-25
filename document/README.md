# 🏦 Bank Transactions — Error Analysis & Fixes

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen?logo=springboot)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-brightgreen?logo=thymeleaf)
![Postgres](https://img.shields.io/badge/PostgreSQL-Database-blue?logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-red?logo=hibernate)
![Docker](https://img.shields.io/badge/Docker-Container-blue?logo=docker)

---

## 📋 Log do Erro
```
INFO: Starting transfer from account 1001 to account 1002 with amount 500.0 
ERROR: Insufficient funds in account: 1001 
INFO: Rolling back transaction due to error 

INFO: Starting transfer from account 1003 to account 1004 with amount 100.0 
ERROR: Could not execute statement; SQL [n/a]; constraint [null]; 
nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
```

### ❌ Erros Identificados
- **Insufficient funds in account**
- **ConstraintViolationException**: `Could not execute statement; SQL [n/a]; constraint [null]`

---

## 🧐 1. Identificação do Problema
Um dos cenários que podem causar o erro **`Insufficient funds in account`** ocorre quando **múltiplas transações são processadas simultaneamente**.  
Nesses casos, o saldo da conta pode não ser atualizado corretamente entre as transações devido à **falta de controle de concorrência**.

Exemplo no código:
```java
if (fromAccount.getBalance() < amount) {
    throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
}
```
➡️ Se outra transação alterar o saldo antes da atualização da primeira, o valor pode ficar inconsistente, causando erro.

O erro **`ConstraintViolationException`** sugere uma **violação de restrição no banco de dados**.  
Isso pode acontecer quando o campo `balance` possui uma restrição de não permitir valores negativos:

```sql
CREATE TABLE public.tb_account (
    id          BIGINT NOT NULL,
    balance     NUMERIC(38, 2) NULL,
    client_name VARCHAR(255) NOT NULL,
    CONSTRAINT tb_account_balance_check CHECK ((balance >= 0)),
    CONSTRAINT tb_account_pkey PRIMARY KEY (id)
);
```

Se uma transação deixar o saldo **negativo**, a restrição será violada.

---

## 🛠️ 2. Sugestões de Correção

### ✅ Controle de Concorrência
- Implementar **lock otimista ou pessimista** para garantir atualização correta do saldo em transações simultâneas.

### ✅ Restrições de Validação
Adicionar anotações na entidade para evitar saldo negativo:
```java
@Check(constraints = "balance >= 0")
@Min(value = 0, message = "O saldo não pode ser negativo")
private BigDecimal balance;
```

### ✅ Arquivos a Ajustar
- `AccountEntity.java` → `com.leokenzley.bank_transactions.entity`
- `AccountServiceImpl.java` → `com.leokenzley.bank_transactions.service.impl`

---

## 📖 3. Relação com Transações e Concorrência
O problema está diretamente relacionado à **execução concorrente de transações**:

1. Duas operações tentam **debitar** a mesma conta ao mesmo tempo.
2. A primeira transação atualiza o saldo com sucesso.
3. A segunda ainda "enxerga" o saldo antigo e tenta debitar.
4. Resultado: saldo negativo ou violação da constraint do banco.

➡️ Isso explica tanto o erro de **saldo insuficiente** quanto a **ConstraintViolationException**.

---

## 📌 Resumo
- **Problema**: Concorrência + validação incorreta de saldo.
- **Impacto**: Erros de saldo insuficiente e violações de constraints.
- **Solução**: Implementar controle de concorrência (otimista/pessimista) e validações adicionais na entidade.