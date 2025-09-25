
Parte 2: Implementação
1. Requisitos de Funcionalidades:
    - Criar operações básicas de manipulação de contas:
    - Criar conta.
    - Creditar valor em uma conta.
    - Debitar valor de uma conta.
    - Transferir valor entre contas.
    - Validar as operações (ex.: não permitir transferências ou débitos que
      deixem a conta com saldo negativo).
    - Criar o Front-end em Thymeleaf, com um acesso Admin Bancário, para criar as
      contas, e um acesso de usuário de conta, para fazer transações, de credito e
      debito.

2. Desafios Técnicos:
    - Implementar o processamento concorrente, garantindo que múltiplas
      transações sejam realizadas sem inconsistências no saldo.
    - Aplicar controle de transações com bloqueio pessimista ou otimista.
3. Tecnologias Sugeridas:
    - Java 8.
    - Spring Boot.
    - JPA/Hibernate.
    - Banco de dados a escolha
    - Testes unitários com JUnit e Mockito.
    - Apresentar no ato da Entrevista Tecnica.