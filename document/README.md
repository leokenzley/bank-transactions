# Bank Transactions — Error Analysis & Fixes

![Java](https://img.shields.io/badge/Java-21-orange?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen?logo=springboot)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-brightgreen?logo=thymeleaf)
![Postgres](https://img.shields.io/badge/PostgreSQL-Database-blue?logo=postgresql)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-red?logo=hibernate)
![Docker](https://img.shields.io/badge/Docker-Container-blue?logo=docker)

---

## Log do Erro
```
INFO: Starting transfer from account 1001 to account 1002 with amount 500.0 
ERROR: Insufficient funds in account: 1001 
INFO: Rolling back transaction due to error 

INFO: Starting transfer from account 1003 to account 1004 with amount 100.0 
ERROR: Could not execute statement; SQL [n/a]; constraint [null]; 
nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
```

### Erros Identificados
- **Insufficient funds in account**
- **ConstraintViolationException**: `Could not execute statement; SQL [n/a]; constraint [null]`

---

## 1. Identificação do Problema
Um dos cenários que podem causar o erro **`Insufficient funds in account`** ocorre quando **múltiplas transações são processadas simultaneamente**.  
Nesses casos, o saldo da conta pode não ser atualizado corretamente entre as transações devido à **falta de controle de concorrência**.

Exemplo no código:
```java
if (fromAccount.getBalance() < amount) {
    throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
}
```
Se outra transação alterar o saldo antes da atualização da primeira, o valor pode ficar inconsistente, causando erro.

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

### Controle de Concorrência
- Implementar **lock otimista ou pessimista** para garantir atualização correta do saldo em transações simultâneas.

### Restrições de Validação
Adicionar anotações na entidade para evitar saldo negativo:
```java
@Check(constraints = "balance >= 0")
@Min(value = 0, message = "O saldo não pode ser negativo")
private BigDecimal balance;
```

### Arquivos a Ajustar
- `AccountEntity.java` → `com.leokenzley.bank_transactions.entity`
- `AccountServiceImpl.java` → `com.leokenzley.bank_transactions.service.impl`
- `AccountRepository.java` → `com.leokenzley.bank_transactions.repository`


### Travar a linha leitura e escrita até que o processamento seja feito.
```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from AccountEntity a where a.id = :id")
  Optional<AccountEntity> findByAccountIdPessimistic(@Param("id") Long accountId);
```
---

## 3. Relação com Transações e Concorrência
O problema está diretamente relacionado à **execução concorrente de transações**:

1. Duas operações tentam **debitar** a mesma conta ao mesmo tempo.
2. A primeira transação atualiza o saldo com sucesso.
3. A segunda ainda "enxerga" o saldo antigo e tenta debitar.
4. Resultado: saldo negativo ou violação da constraint do banco.

#### Isso explica tanto o erro de **saldo insuficiente** quanto a **ConstraintViolationException**.

---

### Se for necessário utilizar Hibernate, é possível utilizar o EntityManager do Spring Data como no exemplo abaixo

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

@Repository
public class AccountRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<AccountEntity> findByAccountIdPessimistic(Long accountId) {
        TypedQuery<AccountEntity> query = entityManager
            .createQuery("SELECT a FROM AccountEntity a WHERE a.id = :id", AccountEntity.class)
            .setParameter("id", accountId)
            .setLockMode(LockModeType.PESSIMISTIC_WRITE);

        try {
            AccountEntity result = query.getSingleResult();
            return Optional.of(result);
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
}
````
### E se alta performance fosse um requisito não funcional, poderiamos utilizar JDBC Template que executa querys nativas com um maior desempenho

```java
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository放下
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Optional<AccountEntity> findByAccountIdPessimistic(Long accountId) {
        String sql = "SELECT id, balance, version FROM account WHERE id = ? FOR UPDATE";
        
        try {
            AccountEntity account = jdbcTemplate.queryForObject(
                sql,
                new Object[]{accountId},
                (rs, rowNum) -> new AccountEntity(
                    rs.getLong("id"),
                    rs.getBigDecimal("balance"),
                    rs.getInt("version")
                )
            );
            return Optional.ofNullable(account);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
```


## Resumo
- **Problema**: Concorrência + validação incorreta de saldo.
- **Impacto**: Erros de saldo insuficiente e violações de constraints.
- **Solução**: Implementar controle de concorrência (otimista/pessimista) e validações adicionais na entidade.
