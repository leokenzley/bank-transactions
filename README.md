# bank-transactions

## Tecnologias
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.2-brightgreen?logo=thymeleaf&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Blue?logo=docker&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-5.6-59666C?logo=hibernate&logoColor=white)


## Resumo da Implementação

Este projeto implementa uma API para gerenciamento de transações bancárias. Para garantir a integridade dos dados em cenários concorrentes, foi utilizado o lock pessimista através do JPA/Hibernate. O lock pessimista impede que múltiplas transações acessem simultaneamente o mesmo registro no banco de dados, evitando problemas como race conditions e inconsistências.

## Vantagens do Lock Pessimista em Alta Volumetria

Em cenários com grande volumetria de requisições, o lock pessimista é mais vantajoso pois garante que apenas uma transação possa modificar um registro por vez, evitando conflitos e retrabalho. Isso reduz a chance de deadlocks e garante maior consistência dos dados, especialmente em operações críticas como movimentações financeiras.

## Infraestrutura

Para subir o ambiente local, utilize Docker:

```bash
    cd ./docker/ && docker compose up -d
```