#### Log do erro:
```
INFO: Starting transfer from account 1001 to account 1002 with amount 500.0 ERROR:
Insufficient funds in account: 1001 INFO: Rolling back transaction due to error INFO:
Starting transfer from account 1003 to account 1004 with amount 100.0 ERROR: Could
not execute statement; SQL [n/a]; constraint [null]; nested exception is
org.hibernate.exception.ConstraintViolationException: could not execute statement
```
 
**ERROR:** Insufficient funds in account e 
**ERROR:** Could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
1. Identificar o problema no código.
```
Um dos possíveis cenários onde pode ocorrer o erro de validação "Insufficient founds in account"
é quando múltiplas transações estão sendo processadas simultaneamente, e o saldo da conta não é atualizado corretamente entre essas transações. 
Isso pode acontecer devido à falta de controle de concorrência, 
onde duas ou mais operações tentam debitar ou transferir fundos da mesma conta ao mesmo tempo, então uma consegue debitar o valor, já a outra falha por o valor do saldo é menor do 
que o valor do início da transferência.

- O erro "Could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement" 
sugere que há uma violação de restrição no banco de dados. 
Análisando o código, não está explícito se existe algum mecanismo de controle de concorrência, como bloqueios otimistas ou pessimistas,
para garantir que o saldo da conta seja atualizado corretamente durante as transações simultâneas.
O erro pode ocorrer quando duas transações tentam atualizar o saldo da mesma conta ao mesmo tempo e 
uma transação que foi iniciada depois tenta atualizar o saldo que já foi modificado pela transação anterior,
entre a execução das linhas que validam se o saldo é suficiente e a atualização do saldo
// Aqui o valor do saldo por exemplo é 1000 e o amount é 500 - Até aqui tudo ok
if (fromAccount.getBalance() < amount) {
    throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
}
Porém se outra transação for iniciada antes da atualização do saldo da primeira transação,
o saldo pode ter sido alterado, por exemplo, para 400, e a validação falha, mesmo que a primeira transação tenha sido bem-sucedida.

Se a coluna do banco de dados que armazena o saldo da conta tiver uma restrição de não permitir valores negativos
CHECK (balance >= 0)
Exemplo de DDL da tabela Account com a restrição de valor negativo:
CREATE TABLE public.tb_account (
	id int8 NOT NULL,
	balance numeric(38, 2) NULL,
	client_name varchar(255) NOT NULL,
	CONSTRAINT tb_account_balance_check CHECK ((balance >= (0)::numeric)), --Restrição
	CONSTRAINT tb_account_pkey PRIMARY KEY (id)
);
O valor sendo negativo vai violar essa restrição, resultando no erro de violação de restrição.
```
2. Sugerir e implementar uma correção.
```
1. Implementar controle de concorrência otimista ou pessimista para garantir que o saldo da conta seja atualizado corretamente durante as transações simultâneas.
2. Adicionar na coluna da classe que mapeia a tabela o valor mínimo para o saldo, para evitar que valores negativos sejam atribuídos.
@Check(constraints = "balance >= 0")
@Min(value = 0, message = "O saldo não pode ser negativo")
A implementação pode ser vista nos arquivos 
AccountEntity.java na pasta com.leokenzley.bank_transactions.entity
e AccountServiceImpl.java na pasta com.leokenzley.bank_transactions.service.impl
```
3. Explicar como o problema está relacionado à transação e concorrência.
```
Um dos possíveis cenários onde pode ocorrer o erro de validação "Insufficient founds in account"
é quando múltiplas transações estão sendo processadas simultaneamente, e o saldo da conta não é atualizado corretamente entre essas transações. 
Isso pode acontecer devido à falta de controle de concorrência, 
onde duas ou mais operações tentam debitar ou transferir fundos da mesma conta ao mesmo tempo, então uma consegue debitar o valor, já a outra falha por o valor do saldo é menor do 
que o valor do início da transferência.

- O erro "Could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement" 
sugere que há uma violação de restrição no banco de dados. 
Análisando o código, não está explícito se existe algum mecanismo de controle de concorrência, como bloqueios otimistas ou pessimistas,
para garantir que o saldo da conta seja atualizado corretamente durante as transações simultâneas.
O erro pode ocorrer quando duas transações tentam atualizar o saldo da mesma conta ao mesmo tempo e 
uma transação que foi iniciada depois tenta atualizar o saldo que já foi modificado pela transação anterior,
entre a execução das linhas que validam se o saldo é suficiente e a atualização do saldo
// Aqui o valor do saldo por exemplo é 1000 e o amount é 500 - Até aqui tudo ok
if (fromAccount.getBalance() < amount) {
    throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
}
Porém se outra transação for iniciada antes da atualização do saldo da primeira transação,
o saldo pode ter sido alterado, por exemplo, para 400, e a validação falha, mesmo que a primeira transação tenha sido bem-sucedida.

Se a coluna do banco de dados que armazena o saldo da conta tiver uma restrição de não permitir valores negativos
CHECK (balance >= 0)
Exemplo de DDL da tabela Account com a restrição de valor negativo:
CREATE TABLE public.tb_account (
	id int8 NOT NULL,
	balance numeric(38, 2) NULL,
	client_name varchar(255) NOT NULL,
	CONSTRAINT tb_account_balance_check CHECK ((balance >= (0)::numeric)), --Restrição
	CONSTRAINT tb_account_pkey PRIMARY KEY (id)
);
O valor sendo negativo vai violar essa restrição, resultando no erro de violação de restrição.
```